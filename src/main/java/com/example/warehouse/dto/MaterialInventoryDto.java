package com.example.warehouse.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MaterialInventoryDto {
    private Long inventoryId;
    @NotNull(message = "Warehouse must be specified")
    private WarehouseDto warehouse;
    @NotNull(message = "Material Type must be specified")
    private MaterialTypeDto materialType;
    @Min(value = 0, message = "Quantity must not be negative")
    private Integer quantity;
}
