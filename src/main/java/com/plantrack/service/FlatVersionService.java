package com.plantrack.service;

import java.util.List;

import org.springframework.core.io.Resource;

import com.plantrack.dto.FlatVersionDTO;
import com.plantrack.model.FlatVersion;

public interface FlatVersionService {

    List<FlatVersion> findAll();

    List<FlatVersion> findActive();

    List<FlatVersion> findByFlat(Long flatId);

    FlatVersion findById(Long id);

    FlatVersion createVersion(Long flatId, FlatVersionDTO dto, String username);

    Resource loadFileAsResource(Long versionId);
}