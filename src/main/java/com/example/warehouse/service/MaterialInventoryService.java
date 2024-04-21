package com.example.warehouse.service;

import com.example.warehouse.dto.MaterialInventoryDto;
import com.example.warehouse.dto.WarehouseMaterialsDto;

import java.util.List;

public interface MaterialInventoryService {
    MaterialInventoryDto addMaterialToWarehouse(Long warehouseId, Long materialTypeId, int quantityToAdd);
    MaterialInventoryDto removeMaterialFromWarehouse(Long warehouseId, Long materialTypeId, int quantityToRemove);
    MaterialInventoryDto moveMaterialBetweenWarehouses(Long sourceWarehouseId, Long destinationWarehouseId, Long materialTypeId, int quantityToMove);
    WarehouseMaterialsDto getWarehouseMaterials(Long warehouseId);
    List<MaterialInventoryDto> getMaterialsByWarehouseId(Long warehouseId);
}
