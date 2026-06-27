package com.plantrack.service;

import java.util.List;

import com.plantrack.dto.PartDTO;
import com.plantrack.model.Part;

public interface PartService {

    List<Part> findAll();

    List<Part> findActive();

    Part findById(Long id);

    Part save(PartDTO dto);

    Part update(Long id, PartDTO dto);

    void delete(Long id);

    List<Part> search(String name);

    List<Part> findByEquipment(Long equipmentId);

    boolean existsByCode(Integer code);
}