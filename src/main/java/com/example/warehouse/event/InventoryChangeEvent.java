package com.example.warehouse.event;


import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class InventoryChangeEvent extends ApplicationEvent {
    private final Long warehouseId;
    private final Long materialTypeId;
    private final int changeQuantity;
    private final String action;

    public InventoryChangeEvent(Object source, Long warehouseId, Long materialTypeId, int changeQuantity, String action) {
        super(source);
        this.warehouseId = warehouseId;
        this.materialTypeId = materialTypeId;
        this.changeQuantity = changeQuantity;
        this.action = action;
    }

}