package com.example.warehouse.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name= "material_type")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MaterialTypeEntity {
    @Id
    @Column(nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true,length = 100)
    private String name;

    private String description;
    private String icon;

    @Column(nullable = false)
    private int maxCapacity;

}
