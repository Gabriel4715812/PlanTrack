package com.plantrack.service.Impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.plantrack.dto.FlatDTO;
import com.plantrack.model.Flat;
import com.plantrack.model.Position;
import com.plantrack.model.User;
import com.plantrack.repository.FlatRepository;
import com.plantrack.repository.PositionRepository;
import com.plantrack.repository.UserRepository;
import com.plantrack.service.FlatService;

import jakarta.annotation.PostConstruct;

@Service
@Transactional
public class FlatServiceImpl implements FlatService {

    private final FlatRepository flatRepository;
    private final PositionRepository positionRepository;
    private final UserRepository userRepository;

    @Value("${plantrack.upload-dir:uploads/flats}")
    private String uploadDir;

    public FlatServiceImpl(
            FlatRepository flatRepository,
            PositionRepository positionRepository,
            UserRepository userRepository) {

        this.flatRepository = flatRepository;
        this.positionRepository = positionRepository;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(getUploadPath());
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear la carpeta de almacenamiento de planos", e);
        }
    }

    @Override
    public List<Flat> findAll() {
        return flatRepository.findAll();
    }

    @Override
    public List<Flat> findActive() {
        return flatRepository.findByActiveTrueOrderByNameAsc();
    }

    @Override
    public Flat findById(Long id) {
        return flatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plano no encontrado"));
    }

    @Override
    public Flat save(FlatDTO dto, String username) {

        String normalizedCode = normalizeText(dto.getCode());

        if (flatRepository.existsByCode(normalizedCode)) {
            throw new RuntimeException("Ya existe un plano con ese código.");
        }

        Position position = positionRepository.findById(dto.getPositionId())
                .orElseThrow(() -> new RuntimeException("Posición no encontrada"));

        User designer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario diseñador no encontrado"));

        Flat flat = new Flat();

        flat.setCode(normalizedCode);
        flat.setName(normalizeText(dto.getName()));
        flat.setDescription(dto.getDescription());
        flat.setPosition(position);
        flat.setDesigner(designer);
        flat.setActive(true);
        flat.setCurrentVersionNumber(1);

        MultipartFile file = dto.getFile();

        if (file != null && !file.isEmpty()) {
            String storedFilePath = storeFile(file, normalizedCode);

            flat.setFileName(StringUtils.cleanPath(file.getOriginalFilename()));
            flat.setFilePath(storedFilePath);
            flat.setContentType(file.getContentType());
        }

        return flatRepository.save(flat);
    }

    @Override
    public Flat update(Long id, FlatDTO dto) {

        Flat flat = findById(id);

        String normalizedCode = normalizeText(dto.getCode());

        if (flatRepository.existsByCodeAndIdNot(normalizedCode, id)) {
            throw new RuntimeException("Ya existe otro plano con ese código.");
        }

        Position position = positionRepository.findById(dto.getPositionId())
                .orElseThrow(() -> new RuntimeException("Posición no encontrada"));

        flat.setCode(normalizedCode);
        flat.setName(normalizeText(dto.getName()));
        flat.setDescription(dto.getDescription());
        flat.setPosition(position);

        MultipartFile file = dto.getFile();

        if (file != null && !file.isEmpty()) {
            String storedFilePath = storeFile(file, normalizedCode);

            flat.setFileName(StringUtils.cleanPath(file.getOriginalFilename()));
            flat.setFilePath(storedFilePath);
            flat.setContentType(file.getContentType());
        }

        return flatRepository.save(flat);
    }

    @Override
    public void delete(Long id) {

        Flat flat = findById(id);

        flat.setActive(false);

        flatRepository.save(flat);
    }

    @Override
    public List<Flat> search(String keyword) {

        if (keyword == null || keyword.trim().isEmpty()) {
            return findActive();
        }

        return flatRepository.searchActiveByKeyword(keyword.trim());
    }

    @Override
    public boolean existsByCode(String code) {

        if (code == null || code.trim().isEmpty()) {
            return false;
        }

        return flatRepository.existsByCode(code.trim());
    }

    @Override
    public Resource loadFileAsResource(Long id) {

        Flat flat = findById(id);

        if (flat.getFilePath() == null || flat.getFilePath().isEmpty()) {
            throw new RuntimeException("El plano no tiene archivo asociado.");
        }

        try {
            Path filePath = Paths.get(flat.getFilePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            }

            throw new RuntimeException("No se pudo leer el archivo del plano.");

        } catch (MalformedURLException e) {
            throw new RuntimeException("Ruta de archivo inválida.", e);
        }
    }

    private String storeFile(MultipartFile file, String flatCode) {

        try {
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

            if (originalFileName.contains("..")) {
                throw new RuntimeException("El nombre del archivo contiene una ruta inválida.");
            }

            String extension = "";

            int dotIndex = originalFileName.lastIndexOf(".");

            if (dotIndex >= 0) {
                extension = originalFileName.substring(dotIndex);
            }

            String safeCode = flatCode.replaceAll("[^a-zA-Z0-9._-]", "_");

            String newFileName = safeCode + "_" + System.currentTimeMillis() + extension;

            Path uploadPath = getUploadPath();

            Files.createDirectories(uploadPath);

            Path targetLocation = uploadPath.resolve(newFileName).normalize();

            Files.copy(
                    file.getInputStream(),
                    targetLocation,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return targetLocation.toString();

        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el archivo del plano.", e);
        }
    }

    private Path getUploadPath() {
        return Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    private String normalizeText(String value) {

        if (value == null) {
            return null;
        }

        return value.trim();
    }
}