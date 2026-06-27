package com.plantrack.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.plantrack.model.Part;

@Repository
public interface PartRepository extends JpaRepository<Part, Long> {

    List<Part> findByActiveTrueOrderByNameAsc();

    List<Part> findByNameContainingIgnoreCaseOrderByNameAsc(String name);

    List<Part> findByActiveTrueAndNameContainingIgnoreCaseOrderByNameAsc(String name);

    List<Part> findByEquipmentId(Long equipmentId);

    List<Part> findByEquipmentIdAndActiveTrue(Long equipmentId);

    Optional<Part> findByCode(Integer code);

    boolean existsByCode(Integer code);

    boolean existsByCodeAndIdNot(Integer code, Long id);
}