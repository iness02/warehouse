package com.example.warehouse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WarehouseDto{
        private  Long id;
        @NotBlank(message = "Name cannot be empty")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        private  String name;

}