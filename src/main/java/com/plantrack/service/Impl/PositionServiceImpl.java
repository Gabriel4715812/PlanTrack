package com.plantrack.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plantrack.dto.PositionDTO;
import com.plantrack.model.Part;
import com.plantrack.model.Position;
import com.plantrack.repository.PartRepository;
import com.plantrack.repository.PositionRepository;
import com.plantrack.service.PositionService;

@Service
@Transactional
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;
    private final PartRepository partRepository;

    public PositionServiceImpl(
            PositionRepository positionRepository,
            PartRepository partRepository) {

        this.positionRepository = positionRepository;
        this.partRepository = partRepository;
    }

    @Override
    public List<Position> findAll() {
        return positionRepository.findAll();
    }

    @Override
    public List<Position> findActive() {
        return positionRepository.findByActiveTrueOrderByNameAsc();
    }

    @Override
    public Position findById(Long id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Posición no encontrada"));
    }

    @Override
    public Position save(PositionDTO dto) {

        if (positionRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Ya existe una posición con ese código.");
        }

        Part part = partRepository.findById(dto.getPartId())
                .orElseThrow(() -> new RuntimeException("Pieza no encontrada"));

        Position position = new Position();

        position.setName(dto.getName());
        position.setCode(dto.getCode());
        position.setDescription(dto.getDescription());
        position.setPart(part);
        position.setActive(true);

        return positionRepository.save(position);
    }

    @Override
    public Position update(Long id, PositionDTO dto) {

        Position position = findById(id);

        if (positionRepository.existsByCodeAndIdNot(dto.getCode(), id)) {
            throw new RuntimeException("Ya existe otra posición con ese código.");
        }

        Part part = partRepository.findById(dto.getPartId())
                .orElseThrow(() -> new RuntimeException("Pieza no encontrada"));

        position.setName(dto.getName());
        position.setCode(dto.getCode());
        position.setDescription(dto.getDescription());
        position.setPart(part);

        return positionRepository.save(position);
    }

    @Override
    public void delete(Long id) {

        Position position = findById(id);

        position.setActive(false);

        positionRepository.save(position);
    }

    @Override
    public List<Position> search(String name) {

        if (name == null || name.trim().isEmpty()) {
            return findActive();
        }

        return positionRepository
                .findByActiveTrueAndNameContainingIgnoreCaseOrderByNameAsc(name.trim());
    }

    @Override
    public List<Position> findByPart(Long partId) {
        return positionRepository.findByPartIdAndActiveTrue(partId);
    }

    @Override
    public boolean existsByCode(Integer code) {
        return positionRepository.existsByCode(code);
    }
}