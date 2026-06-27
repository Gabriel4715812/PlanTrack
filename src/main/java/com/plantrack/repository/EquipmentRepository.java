package com.plantrack.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.plantrack.model.Equipment;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    List<Equipment> findByActiveTrue();

    Optional<Equipment> findByCode(Integer code);

    boolean existsByCode(Integer code);

    List<Equipment> findByNameContainingIgnoreCase(String name);

    List<Equipment> findBySectorId(Long sectorId);

}