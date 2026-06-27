package com.plantrack.service.Impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.plantrack.model.Flat;
import com.plantrack.model.QrCode;
import com.plantrack.repository.FlatRepository;
import com.plantrack.repository.QrCodeRepository;
import com.plantrack.service.QrCodeService;

import jakarta.annotation.PostConstruct;

@Service
@Transactional
public class QrCodeServiceImpl implements QrCodeService {

    private final QrCodeRepository qrCodeRepository;
    private final FlatRepository flatRepository;

    @Value("${plantrack.qr-upload-dir:uploads/qr}")
    private String qrUploadDir;

    public QrCodeServiceImpl(
            QrCodeRepository qrCodeRepository,
            FlatRepository flatRepository) {

        this.qrCodeRepository = qrCodeRepository;
        this.flatRepository = flatRepository;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(getQrUploadPath());
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear la carpeta de códigos QR.", e);
        }
    }

    @Override
    public List<QrCode> findAll() {
        return qrCodeRepository.findAll();
    }

    @Override
    public List<QrCode> findActive() {
        return qrCodeRepository.findByActiveTrueOrderByCreatedAtDesc();
    }

    @Override
    public QrCode findById(Long id) {
        return qrCodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Código QR no encontrado"));
    }

    @Override
    public QrCode findByFlat(Long flatId) {
        return qrCodeRepository.findByFlatIdAndActiveTrue(flatId)
                .orElseThrow(() -> new RuntimeException("El plano no tiene código QR generado"));
    }

    @Override
    public QrCode generateForFlat(Long flatId, String baseUrl) {

        Flat flat = flatRepository.findById(flatId)
                .orElseThrow(() -> new RuntimeException("Plano no encontrado"));

        /*
         * Si el plano ya tiene un QR activo, lo devolvemos.
         * Así evitamos romper la restricción UNIQUE(flat_id).
         */
        return qrCodeRepository.findByFlatIdAndActiveTrue(flatId)
                .orElseGet(() -> createQrCode(flat, baseUrl));
    }

    private QrCode createQrCode(Flat flat, String baseUrl) {

        String targetUrl = buildTargetUrl(baseUrl, flat.getId());

        String qrValue = targetUrl;

        String qrImagePath = generateQrImage(flat, qrValue);

        QrCode qrCode = new QrCode();

        qrCode.setFlat(flat);
        qrCode.setQrValue(qrValue);
        qrCode.setTargetUrl(targetUrl);
        qrCode.setQrImagePath(qrImagePath);
        qrCode.setActive(true);

        return qrCodeRepository.save(qrCode);
    }

    @Override
    public Resource loadQrImageAsResource(Long qrId) {

        QrCode qrCode = findById(qrId);

        if (qrCode.getQrImagePath() == null || qrCode.getQrImagePath().isBlank()) {
            throw new RuntimeException("El código QR no tiene imagen asociada.");
        }

        try {
            Path imagePath = Paths.get(qrCode.getQrImagePath()).normalize();

            Resource resource = new UrlResource(imagePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            }

            throw new RuntimeException("No se pudo leer la imagen del código QR.");

        } catch (MalformedURLException e) {
            throw new RuntimeException("Ruta de imagen QR inválida.", e);
        }
    }

    private String generateQrImage(Flat flat, String qrValue) {

        try {
            int width = 350;
            int height = 350;

            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            BitMatrix bitMatrix = qrCodeWriter.encode(
                    qrValue,
                    BarcodeFormat.QR_CODE,
                    width,
                    height
            );

            String safeCode = flat.getCode()
                    .replaceAll("[^a-zA-Z0-9._-]", "_");

            String fileName = "QR_" + safeCode + "_" + System.currentTimeMillis() + ".png";

            Path uploadPath = getQrUploadPath();

            Files.createDirectories(uploadPath);

            Path targetLocation = uploadPath.resolve(fileName).normalize();

            MatrixToImageWriter.writeToPath(
                    bitMatrix,
                    "PNG",
                    targetLocation
            );

            return targetLocation.toString();

        } catch (WriterException | IOException e) {
            throw new RuntimeException("No se pudo generar la imagen del código QR.", e);
        }
    }

    private String buildTargetUrl(String baseUrl, Long flatId) {

        String cleanedBaseUrl = baseUrl;

        if (cleanedBaseUrl.endsWith("/")) {
            cleanedBaseUrl = cleanedBaseUrl.substring(0, cleanedBaseUrl.length() - 1);
        }

        return cleanedBaseUrl + "/flats/detail/" + flatId;
    }

    private Path getQrUploadPath() {
        return Paths.get(qrUploadDir).toAbsolutePath().normalize();
    }
}