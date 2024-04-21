package com.example.warehouse.controller;

import com.example.warehouse.dto.MaterialInventoryDto;
import com.example.warehouse.dto.WarehouseMaterialsDto;
import com.example.warehouse.exceptions.WarehouseNotFoundException;
import com.example.warehouse.service.MaterialInventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/warehouses/{warehouseId}/materials")
public class MaterialInventoryController {

    private final MaterialInventoryService materialInventoryService;

    public MaterialInventoryController(MaterialInventoryService materialInventoryService) {
        this.materialInventoryService = materialInventoryService;
    }

    /**
     * Adds a specified quantity of a material to a warehouse.
     *
     * @param warehouseId    the ID of the warehouse where the material will be added
     * @param materialTypeId the ID of the material type to be added
     * @param quantityToAdd  the quantity of the material to add
     * @return the updated inventory as a DTO, or an error message if unsuccessful
     */
    @PostMapping("/{materialTypeId}")
    public ResponseEntity<MaterialInventoryDto> addMaterialToWarehouse(@PathVariable Long warehouseId,
                                                                       @PathVariable Long materialTypeId,
                                                                       @RequestParam int quantityToAdd) {
        MaterialInventoryDto result = materialInventoryService.addMaterialToWarehouse(warehouseId, materialTypeId, quantityToAdd);
        return ResponseEntity.ok(result);
    }


    /**
     * Removes a specified quantity of material from a warehouse.
     *
     * @param warehouseId      the ID of the warehouse from which the material will be removed
     * @param materialTypeId   the ID of the material type to be removed
     * @param quantityToRemove the quantity of the material to remove
     * @return the updated inventory as a DTO, or null if the entire inventory is depleted
     */
    @DeleteMapping("/{materialTypeId}")
    public ResponseEntity<MaterialInventoryDto> removeMaterialFromWarehouse(@PathVariable Long warehouseId,
                                                                            @PathVariable Long materialTypeId,
                                                                            @RequestParam int quantityToRemove) {
        MaterialInventoryDto result = materialInventoryService.removeMaterialFromWarehouse(warehouseId, materialTypeId, quantityToRemove);
        if (result == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(result);
    }

    /**
     * Moves a specified quantity of material from one warehouse to another.
     *
     * @param warehouseId            the ID of the source warehouse
     * @param destinationWarehouseId the ID of the destination warehouse
     * @param materialTypeId         the ID of the material type to move
     * @param quantityToMove         the quantity of the material to move
     * @return the updated inventory of the destination warehouse as a DTO
     */
    @PatchMapping("/{materialTypeId}/transfer-to/{destinationWarehouseId}")
    public ResponseEntity<MaterialInventoryDto> moveMaterialBetweenWarehouses(@PathVariable Long warehouseId,
                                                                              @PathVariable Long destinationWarehouseId,
                                                                              @PathVariable Long materialTypeId,
                                                                              @RequestParam int quantityToMove) {
        MaterialInventoryDto result = materialInventoryService.moveMaterialBetweenWarehouses(warehouseId, destinationWarehouseId, materialTypeId, quantityToMove);
        return ResponseEntity.ok(result);
    }


    /**
     * Retrieves the details of a specific warehouse including a list of all materials stored.
     *
     * @param warehouseId the ID of the warehouse for which details are required
     * @return a ResponseEntity containing the warehouse details and its materials as a DTO. If the warehouse is not found, a 404 error is returned.
     */
    @GetMapping
    public ResponseEntity<WarehouseMaterialsDto> getWarehouseMaterials(@PathVariable Long warehouseId) {

        WarehouseMaterialsDto result = materialInventoryService.getWarehouseMaterials(warehouseId);
        return ResponseEntity.ok(result);

    }
}
