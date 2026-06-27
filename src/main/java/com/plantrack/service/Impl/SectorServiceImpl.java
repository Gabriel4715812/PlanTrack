package com.plantrack.service.Impl;

import com.plantrack.dto.SectorDTO;
import com.plantrack.model.Sector;
import com.plantrack.repository.SectorRepository;
import com.plantrack.service.SectorService;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectorServiceImpl implements SectorService {

    private final SectorRepository sectorRepository;

    public SectorServiceImpl(SectorRepository sectorRepository) {
        this.sectorRepository = sectorRepository;
    }

    @Override
    public List<Sector> findAll() {
        return sectorRepository.findAll();
    }

    @Override
    public List<Sector> findActive() {
        return sectorRepository.findByActiveTrueOrderByNameAsc();
    }

    @Override
    public Sector findById(Long id) {
        return sectorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sector no encontrado"));
    }

    @Override
    public Sector save(SectorDTO dto) {

        String code = normalize(dto.getCode());

        if (sectorRepository.existsByCode(code)) {
            throw new RuntimeException("Ya existe un sector con ese código.");
        }

        Sector sector = new Sector();

        sector.setName(normalize(dto.getName()));
        sector.setCode(code);
        sector.setActive(true);

        return sectorRepository.save(sector);
    }

    @Override
    public Sector update(Long id, SectorDTO dto) {

        Sector sector = findById(id);

        String code = normalize(dto.getCode());

        if (sectorRepository.existsByCodeAndIdNot(code, id)) {
            throw new RuntimeException("Ya existe otro sector con ese código.");
        }

        sector.setName(normalize(dto.getName()));
        sector.setCode(code);

        return sectorRepository.save(sector);
    }

    @Override
    public void delete(Long id) {

        Sector sector = findById(id);

        sector.setActive(false);

        sectorRepository.save(sector);
    }

    @Override
    public List<Sector> search(String keyword) {

        if (keyword == null || keyword.trim().isEmpty()) {
            return findActive();
        }

        return sectorRepository.searchActiveByKeyword(keyword.trim());
    }

    private String normalize(String value) {

        if (value == null) {
            return null;
        }

        return value.trim();
    }
}