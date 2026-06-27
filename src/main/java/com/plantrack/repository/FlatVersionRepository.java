package com.plantrack.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.plantrack.model.FlatVersion;

@Repository
public interface FlatVersionRepository extends JpaRepository<FlatVersion, Long> {

    List<FlatVersion> findByActiveTrueOrderByCreatedAtDesc();

    List<FlatVersion> findByFlatIdOrderByVersionNumberDesc(Long flatId);

    List<FlatVersion> findByFlatIdAndActiveTrueOrderByVersionNumberDesc(Long flatId);

    Optional<FlatVersion> findTopByFlatIdOrderByVersionNumberDesc(Long flatId);

    Optional<FlatVersion> findTopByFlatIdAndActiveTrueOrderByVersionNumberDesc(Long flatId);

    boolean existsByFlatIdAndVersionNumber(Long flatId, Integer versionNumber);
}