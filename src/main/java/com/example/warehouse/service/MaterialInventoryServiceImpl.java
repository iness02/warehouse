package com.example.warehouse.service;

import com.example.warehouse.dto.MaterialInventoryDto;
import com.example.warehouse.dto.WarehouseMaterialsDto;
import com.example.warehouse.entity.MaterialInventoryEntity;
import com.example.warehouse.entity.MaterialTypeEntity;
import com.example.warehouse.entity.WarehouseEntity;
import com.example.warehouse.event.InventoryChangeEvent;
import com.example.warehouse.exceptions.*;
import com.example.warehouse.repository.MaterialInventoryRepository;
import com.example.warehouse.repository.MaterialTypeRepository;
import com.example.warehouse.repository.WarehouseRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaterialInventoryServiceImpl implements MaterialInventoryService{

    private final WarehouseRepository warehouseRepository;
    private final MaterialInventoryRepository materialInventoryRepository;
    private final MaterialTypeRepository materialTypeRepository;
    private final ApplicationEventPublisher publisher;
    private final ModelMapper modelMapper;


    public MaterialInventoryServiceImpl(WarehouseRepository warehouseRepository,
                                        MaterialInventoryRepository materialInventoryRepository,
                                        MaterialTypeRepository materialTypeRepository,
                                        ApplicationEventPublisher publisher, ModelMapper modelMapper) {
        this.warehouseRepository = warehouseRepository;
        this.materialInventoryRepository = materialInventoryRepository;
        this.materialTypeRepository = materialTypeRepository;
        this.publisher = publisher;
        this.modelMapper = modelMapper;
    }

    //Method for adding material to warehouse
    @Override
    public MaterialInventoryDto addMaterialToWarehouse(Long warehouseId,
                                                       Long materialTypeId,
                                                       int quantityToAdd) {
        //check if warehouse exists with given id
        WarehouseEntity warehouse = validateWarehouseExists(warehouseId);
        //check if materialType exists with given id
        MaterialTypeEntity materialType = validateMaterialTypeExists(materialTypeId);

        //trying to get or create inventory based on warehouse id and materialType
        MaterialInventoryEntity inventory = materialInventoryRepository
                .findByWarehouseAndMaterialType(warehouse, materialType)
                .orElse(new MaterialInventoryEntity());

        //if inventory doesn't exist create a new one
        if (inventory.getInventoryId() == null) {
            validateCapacity(materialType, quantityToAdd);
            inventory.setWarehouse(warehouse);
            inventory.setMaterialType(materialType);
            inventory.setQuantity(quantityToAdd);
        } else {
            // if inventory already exists check if new quantity
            // doesn't exceed maximum quantity of given material
            int newQuantity = inventory.getQuantity() + quantityToAdd;
            validateCapacity(materialType, newQuantity);
            inventory.setQuantity(newQuantity);
        }
        // saving inventory to database
        MaterialInventoryEntity savedInventory = materialInventoryRepository.save(inventory);
        // Publish an event after updating inventory
        InventoryChangeEvent event = new InventoryChangeEvent(this, warehouseId, materialTypeId, quantityToAdd, "add");
        publisher.publishEvent(event);
        return modelMapper.map(savedInventory, MaterialInventoryDto.class);
    }


    //Method for removing material from warehouse
    @Override
    @Transactional
    public MaterialInventoryDto removeMaterialFromWarehouse(Long warehouseId, Long materialTypeId, int quantityToRemove) {
        //check if warehouse exists with given id
        WarehouseEntity warehouse = validateWarehouseExists(warehouseId);
        //check if materialType exists with given id
        MaterialTypeEntity materialType = validateMaterialTypeExists(materialTypeId);
        //check if inventory exists with warehouse id and material type
        MaterialInventoryEntity inventory = validateInventoryExists(warehouse,materialType);
        //the quantity we want to remove couldn't be greater than quantity that exists in inventory
        if (inventory.getQuantity() < quantityToRemove) {
            throw new InsufficientInventoryException("Attempting to remove " + quantityToRemove + " units, but only " + inventory.getQuantity() + " units are available.");
        }

        inventory.setQuantity(inventory.getQuantity() - quantityToRemove);
        // Publish an event after removing inventory
        InventoryChangeEvent removalEvent = new InventoryChangeEvent(this, warehouseId, materialTypeId, -quantityToRemove, "remove");
        publisher.publishEvent(removalEvent);

        // If the quantity now becomes 0, we should remove the inventory record
        if (inventory.getQuantity() == 0) {
            materialInventoryRepository.delete(inventory);
            return null;
        } else {
            MaterialInventoryEntity savedInventory = materialInventoryRepository.save(inventory);
            return modelMapper.map(savedInventory, MaterialInventoryDto.class);
        }
    }

    //method for moving material from one warehouse to another
    @Override
    @Transactional
    public MaterialInventoryDto moveMaterialBetweenWarehouses(Long sourceWarehouseId, Long destinationWarehouseId, Long materialTypeId, int quantityToMove) {
        // //check if source warehouse exists with given id
        WarehouseEntity sourceWarehouse = warehouseRepository.findById(sourceWarehouseId)
                .orElseThrow(() -> new WarehouseNotFoundException("Source warehouse not found with ID: " + sourceWarehouseId));
        //check if destination warehouse exists with given id
        WarehouseEntity destinationWarehouse = warehouseRepository.findById(destinationWarehouseId)
                .orElseThrow(() -> new WarehouseNotFoundException("Destination warehouse not found with ID: " + destinationWarehouseId));
        //check if material exists with given id
        MaterialTypeEntity materialType = materialTypeRepository.findById(materialTypeId)
                .orElseThrow(() -> new MaterialTypeNotFoundException("Material type not found with ID: " + materialTypeId));

        // Fetch inventory records
        MaterialInventoryEntity sourceInventory = materialInventoryRepository
                .findByWarehouseAndMaterialType(sourceWarehouse, materialType)
                .orElseThrow(() -> new InventoryNotFoundException("Material not found in source warehouse with ID: " + sourceWarehouseId + " and Material Type ID: " + materialTypeId));

        MaterialInventoryEntity destinationInventory = materialInventoryRepository
                .findByWarehouseAndMaterialType(destinationWarehouse, materialType)
                .orElse(new MaterialInventoryEntity(destinationWarehouse, materialType, 0));


        // Check if source has enough material
        if (sourceInventory.getQuantity() < quantityToMove) {
            throw new InsufficientInventoryException("Insufficient material in source warehouse. Attempting to move " + quantityToMove + " units, but only " + sourceInventory.getQuantity() + " units are available.");
        }

        // Check if destination can hold more material
        int destinationPotentialNewQuantity = destinationInventory.getQuantity() + quantityToMove;

        if (destinationPotentialNewQuantity > materialType.getMaxCapacity()) {
            throw new CapacityExceededException("Destination warehouse cannot accommodate the materials due to max capacity constraints. Max capacity is " + materialType.getMaxCapacity() + ", attempted new quantity is " + destinationPotentialNewQuantity + ".");
        }

        // Update inventories
        sourceInventory.setQuantity(sourceInventory.getQuantity() - quantityToMove);
        destinationInventory.setQuantity(destinationInventory.getQuantity() + quantityToMove);

        // Publish events after moving inventory
        InventoryChangeEvent sourceEvent = new InventoryChangeEvent(this, sourceWarehouseId, materialTypeId, -quantityToMove, "move from");
        InventoryChangeEvent destinationEvent = new InventoryChangeEvent(this, destinationWarehouseId, materialTypeId, quantityToMove, "move to");
        publisher.publishEvent(sourceEvent);
        publisher.publishEvent(destinationEvent);

        // Save changes
        materialInventoryRepository.save(sourceInventory);
        materialInventoryRepository.save(destinationInventory);

        // Return the updated inventory of the destination warehouse
        return modelMapper.map(destinationInventory, MaterialInventoryDto.class);
    }

    //method for getting warehouse (its name and materials  there)
    @Override
    public WarehouseMaterialsDto getWarehouseMaterials(Long warehouseId) {
        WarehouseEntity warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found with ID: " + warehouseId));

        List<MaterialInventoryDto> materials = getMaterialsByWarehouseId(warehouseId);

        WarehouseMaterialsDto response = new WarehouseMaterialsDto();
        response.setId(warehouse.getId());
        response.setName(warehouse.getName());
        response.setMaterials(materials);

        return response;
    }

    @Override
    public List<MaterialInventoryDto> getMaterialsByWarehouseId(Long warehouseId) {
        List<MaterialInventoryEntity> materialInventoryEntities = materialInventoryRepository.findByWarehouseId(warehouseId);
        return materialInventoryEntities.stream()
                .map(entity -> modelMapper.map(entity, MaterialInventoryDto.class))
                .collect(Collectors.toList());
    }
    private void validateCapacity(MaterialTypeEntity materialType, int quantity) {
        if (quantity > materialType.getMaxCapacity()) {
            throw new CapacityExceededException("Quantity " + quantity + " exceeds the maximum capacity for this material type: " + materialType.getName());
        }
    }

    private WarehouseEntity validateWarehouseExists(Long warehouseId) {
        return warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found with ID: " + warehouseId));
    }

    private MaterialTypeEntity validateMaterialTypeExists(Long materialTypeId) {
        return materialTypeRepository.findById(materialTypeId)
                .orElseThrow(() -> new MaterialTypeNotFoundException("Material Type not found with ID: " + materialTypeId));
    }

    private MaterialInventoryEntity validateInventoryExists( WarehouseEntity warehouse, MaterialTypeEntity materialType) {
        return materialInventoryRepository.findByWarehouseAndMaterialType(warehouse, materialType)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory record not found for warehouse ID: " + warehouse.getId() + " and material type ID: " + materialType.getId()));
    }
}
