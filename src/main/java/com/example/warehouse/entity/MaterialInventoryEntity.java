package com.example.warehouse.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "material_inventory", indexes = {
        @Index(name = "idx_warehouse", columnList = "warehouse_id"),
        @Index(name = "idx_material_type", columnList = "material_type_id")
})
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class MaterialInventoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private WarehouseEntity warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_type_id", nullable = false)
    private MaterialTypeEntity materialType;

    private Integer quantity;

    public MaterialInventoryEntity(WarehouseEntity warehouse, MaterialTypeEntity materialType, int quantity) {
        this.warehouse = warehouse;
        this.materialType =materialType;
        this.quantity=quantity;

    }
}
