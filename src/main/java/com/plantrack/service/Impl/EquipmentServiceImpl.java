package com.plantrack.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plantrack.dto.EquipmentDTO;
import com.plantrack.model.Equipment;
import com.plantrack.model.Sector;
import com.plantrack.repository.EquipmentRepository;
import com.plantrack.repository.SectorRepository;
import com.plantrack.service.EquipmentService;

@Service
@Transactional
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final SectorRepository sectorRepository;

    public EquipmentServiceImpl(
            EquipmentRepository equipmentRepository,
            SectorRepository sectorRepository) {

        this.equipmentRepository = equipmentRepository;
        this.sectorRepository = sectorRepository;
    }

    @Override
    public List<Equipment> findAll() {
        return equipmentRepository.findAll();
    }

    @Override
    public List<Equipment> findActive() {
        return equipmentRepository.findByActiveTrue();
    }

    @Override
    public Equipment findById(Long id) {

        return equipmentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Equipo no encontrado"));
    }

    @Override
    public Equipment save(EquipmentDTO dto) {

        if (equipmentRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("El código ya existe.");
        }

        Sector sector = sectorRepository.findById(dto.getSectorId())
                .orElseThrow(() ->
                        new RuntimeException("Sector inexistente."));

        Equipment equipment = new Equipment();

        equipment.setName(dto.getName());
        equipment.setCode(dto.getCode());
        equipment.setDescription(dto.getDescription());
        equipment.setSector(sector);
        equipment.setActive(true);

        return equipmentRepository.save(equipment);
    }

    @Override
    public Equipment update(Long id, EquipmentDTO dto) {

        Equipment equipment = findById(id);

        equipment.setName(dto.getName());
        equipment.setCode(dto.getCode());
        equipment.setDescription(dto.getDescription());

        Sector sector = sectorRepository.findById(dto.getSectorId())
                .orElseThrow(() ->
                        new RuntimeException("Sector inexistente."));

        equipment.setSector(sector);

        return equipmentRepository.save(equipment);
    }

    @Override
    public void delete(Long id) {

        Equipment equipment = findById(id);

        equipment.setActive(false);

        equipmentRepository.save(equipment);
    }

    @Override
    public List<Equipment> search(String name) {

        return equipmentRepository
                .findByNameContainingIgnoreCase(name);
    }

    @Override
    public boolean existsByCode(Integer code) {

        return equipmentRepository.existsByCode(code);
    }

    @Override
    public List<Equipment> findBySector(Long sectorId) {

        return equipmentRepository.findBySectorId(sectorId);
    }

}