package com.plantrack.service;

import java.util.List;

import org.springframework.core.io.Resource;

import com.plantrack.dto.FlatDTO;
import com.plantrack.model.Flat;

public interface FlatService {

    List<Flat> findAll();

    List<Flat> findActive();

    Flat findById(Long id);

    Flat save(FlatDTO dto, String username);

    Flat update(Long id, FlatDTO dto);

    void delete(Long id);

    List<Flat> search(String keyword);

    boolean existsByCode(String code);

    Resource loadFileAsResource(Long id);
}