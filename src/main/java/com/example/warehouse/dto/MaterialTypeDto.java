package com.example.warehouse.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MaterialTypeDto {
    private Long id;
    @NotBlank(message = "Name cannot be empty")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;
    private String icon;
    @Min(value = 1, message = "Maximum capacity must be at least 1")
    private int maxCapacity;

}
