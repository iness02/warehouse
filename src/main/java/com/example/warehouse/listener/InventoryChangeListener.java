package com.example.warehouse.listener;

import com.example.warehouse.event.InventoryChangeEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryChangeListener {

    @EventListener
    public void onInventoryChange(InventoryChangeEvent event) {
        System.out.println("Inventory change event received:");
        System.out.println("Action: " + event.getAction() + ", Warehouse ID: " + event.getWarehouseId() + ", Material Type ID: " + event.getMaterialTypeId() + ", Quantity Changed: " + event.getChangeQuantity());

    }
}