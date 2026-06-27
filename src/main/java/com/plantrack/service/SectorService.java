package com.plantrack.service;

import com.plantrack.dto.SectorDTO;
import com.plantrack.model.Sector;

import java.util.List;

public interface SectorService {

    List<Sector> findAll();

    List<Sector> findActive();

    Sector findById(Long id);

    Sector save(SectorDTO dto);

    Sector update(Long id, SectorDTO dto);

    void delete(Long id);

    List<Sector> search(String keyword);
}