package com.example.warehouse.repository;

import com.example.warehouse.entity.MaterialInventoryEntity;
import com.example.warehouse.entity.MaterialTypeEntity;
import com.example.warehouse.entity.WarehouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialInventoryRepository extends JpaRepository<MaterialInventoryEntity, Long> {


    Optional<MaterialInventoryEntity> findByWarehouseAndMaterialType(WarehouseEntity warehouse, MaterialTypeEntity materialType);

    List<MaterialInventoryEntity> findByWarehouseId(Long warehouseId);
}