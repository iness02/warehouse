package com.example.warehouse.repository;

import com.example.warehouse.entity.MaterialTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialTypeRepository extends JpaRepository<MaterialTypeEntity, Long> {
}