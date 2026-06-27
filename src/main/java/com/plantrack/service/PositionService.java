package com.plantrack.service;

import java.util.List;

import com.plantrack.dto.PositionDTO;
import com.plantrack.model.Position;

public interface PositionService {

    List<Position> findAll();

    List<Position> findActive();

    Position findById(Long id);

    Position save(PositionDTO dto);

    Position update(Long id, PositionDTO dto);

    void delete(Long id);

    List<Position> search(String name);

    List<Position> findByPart(Long partId);

    boolean existsByCode(Integer code);
}