package com.plantrack.service;

import java.util.List;

import com.plantrack.dto.EquipmentDTO;
import com.plantrack.model.Equipment;

public interface EquipmentService {

    List<Equipment> findAll();

    List<Equipment> findActive();

    Equipment findById(Long id);

    Equipment save(EquipmentDTO dto);

    Equipment update(Long id, EquipmentDTO dto);

    void delete(Long id);

    List<Equipment> search(String name);

    boolean existsByCode(Integer code);

    List<Equipment> findBySector(Long sectorId);

}