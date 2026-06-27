package com.plantrack.service;

import java.util.List;

import org.springframework.core.io.Resource;

import com.plantrack.model.QrCode;

public interface QrCodeService {

    List<QrCode> findAll();

    List<QrCode> findActive();

    QrCode findById(Long id);

    QrCode findByFlat(Long flatId);

    QrCode generateForFlat(Long flatId, String baseUrl);

    Resource loadQrImageAsResource(Long qrId);
}