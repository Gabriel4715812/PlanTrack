package com.plantrack.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plantrack.dto.PartDTO;
import com.plantrack.model.Equipment;
import com.plantrack.model.Part;
import com.plantrack.repository.EquipmentRepository;
import com.plantrack.repository.PartRepository;
import com.plantrack.service.PartService;

@Service
@Transactional
public class PartServiceImpl implements PartService {

    private final PartRepository partRepository;
    private final EquipmentRepository equipmentRepository;

    public PartServiceImpl(
            PartRepository partRepository,
            EquipmentRepository equipmentRepository) {

        this.partRepository = partRepository;
        this.equipmentRepository = equipmentRepository;
    }

    @Override
    public List<Part> findAll() {
        return partRepository.findAll();
    }

    @Override
    public List<Part> findActive() {
        return partRepository.findByActiveTrueOrderByNameAsc();
    }

    @Override
    public Part findById(Long id) {
        return partRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pieza no encontrada"));
    }

    @Override
    public Part save(PartDTO dto) {

        if (partRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Ya existe una pieza con ese código.");
        }

        Equipment equipment = equipmentRepository.findById(dto.getEquipmentId())
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        Part part = new Part();

        part.setName(dto.getName());
        part.setCode(dto.getCode());
        part.setDescription(dto.getDescription());
        part.setEquipment(equipment);
        part.setActive(true);

        return partRepository.save(part);
    }

    @Override
    public Part update(Long id, PartDTO dto) {

        Part part = findById(id);

        if (partRepository.existsByCodeAndIdNot(dto.getCode(), id)) {
            throw new RuntimeException("Ya existe otra pieza con ese código.");
        }

        Equipment equipment = equipmentRepository.findById(dto.getEquipmentId())
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        part.setName(dto.getName());
        part.setCode(dto.getCode());
        part.setDescription(dto.getDescription());
        part.setEquipment(equipment);

        return partRepository.save(part);
    }

    @Override
    public void delete(Long id) {

        Part part = findById(id);

        part.setActive(false);

        partRepository.save(part);
    }

    @Override
    public List<Part> search(String name) {

        if (name == null || name.trim().isEmpty()) {
            return findActive();
        }

        return partRepository.findByActiveTrueAndNameContainingIgnoreCaseOrderByNameAsc(name.trim());
    }

    @Override
    public List<Part> findByEquipment(Long equipmentId) {
        return partRepository.findByEquipmentIdAndActiveTrue(equipmentId);
    }

    @Override
    public boolean existsByCode(Integer code) {
        return partRepository.existsByCode(code);
    }
}