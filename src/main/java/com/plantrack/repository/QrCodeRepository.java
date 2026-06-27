package com.plantrack.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.plantrack.model.QrCode;

@Repository
public interface QrCodeRepository extends JpaRepository<QrCode, Long> {

    List<QrCode> findByActiveTrueOrderByCreatedAtDesc();

    Optional<QrCode> findByFlatId(Long flatId);

    Optional<QrCode> findByFlatIdAndActiveTrue(Long flatId);

    boolean existsByFlatId(Long flatId);
}