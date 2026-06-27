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

import com.plantrack.dto.FlatVersionDTO;
import com.plantrack.model.Flat;
import com.plantrack.model.FlatVersion;
import com.plantrack.model.User;
import com.plantrack.repository.FlatRepository;
import com.plantrack.repository.FlatVersionRepository;
import com.plantrack.repository.UserRepository;
import com.plantrack.service.FlatVersionService;

import jakarta.annotation.PostConstruct;

@Service
@Transactional
public class FlatVersionServiceImpl implements FlatVersionService {

    private final FlatVersionRepository flatVersionRepository;
    private final FlatRepository flatRepository;
    private final UserRepository userRepository;

    @Value("${plantrack.version-upload-dir:uploads/versions}")
    private String versionUploadDir;

    public FlatVersionServiceImpl(
            FlatVersionRepository flatVersionRepository,
            FlatRepository flatRepository,
            UserRepository userRepository) {

        this.flatVersionRepository = flatVersionRepository;
        this.flatRepository = flatRepository;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(getVersionUploadPath());
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear la carpeta de versiones de planos.", e);
        }
    }

    @Override
    public List<FlatVersion> findAll() {
        return flatVersionRepository.findAll();
    }

    @Override
    public List<FlatVersion> findActive() {
        return flatVersionRepository.findByActiveTrueOrderByCreatedAtDesc();
    }

    @Override
    public List<FlatVersion> findByFlat(Long flatId) {
        return flatVersionRepository.findByFlatIdAndActiveTrueOrderByVersionNumberDesc(flatId);
    }

    @Override
    public FlatVersion findById(Long id) {
        return flatVersionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Versión de plano no encontrada"));
    }

    @Override
    public FlatVersion createVersion(Long flatId, FlatVersionDTO dto, String username) {

        Flat flat = flatRepository.findById(flatId)
                .orElseThrow(() -> new RuntimeException("Plano no encontrado"));

        User createdBy = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        MultipartFile file = dto.getFile();

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Debe cargar el archivo correspondiente a la nueva versión.");
        }

        Integer nextVersionNumber = calculateNextVersionNumber(flat);

        if (flatVersionRepository.existsByFlatIdAndVersionNumber(flatId, nextVersionNumber)) {
            throw new RuntimeException("Ya existe una versión con ese número para este plano.");
        }

        String storedFilePath = storeFile(file, flat.getCode(), nextVersionNumber);

        FlatVersion version = new FlatVersion();

        version.setFlat(flat);
        version.setVersionNumber(nextVersionNumber);
        version.setChangeDescription(normalize(dto.getChangeDescription()));
        version.setFileName(StringUtils.cleanPath(file.getOriginalFilename()));
        version.setFilePath(storedFilePath);
        version.setContentType(file.getContentType());
        version.setCreatedBy(createdBy);
        version.setActive(true);

        FlatVersion savedVersion = flatVersionRepository.save(version);

        /*
         * Actualizamos también el plano principal para que siempre apunte
         * al archivo y número de versión más reciente.
         */
        flat.setCurrentVersionNumber(nextVersionNumber);
        flat.setFileName(savedVersion.getFileName());
        flat.setFilePath(savedVersion.getFilePath());
        flat.setContentType(savedVersion.getContentType());

        flatRepository.save(flat);

        return savedVersion;
    }

    @Override
    public Resource loadFileAsResource(Long versionId) {

        FlatVersion version = findById(versionId);

        if (version.getFilePath() == null || version.getFilePath().isBlank()) {
            throw new RuntimeException("La versión no tiene archivo asociado.");
        }

        try {
            Path filePath = Paths.get(version.getFilePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            }

            throw new RuntimeException("No se pudo leer el archivo de la versión.");

        } catch (MalformedURLException e) {
            throw new RuntimeException("Ruta de archivo inválida.", e);
        }
    }

    private Integer calculateNextVersionNumber(Flat flat) {

        Long flatId = flat.getId();

        return flatVersionRepository.findTopByFlatIdOrderByVersionNumberDesc(flatId)
                .map(lastVersion -> lastVersion.getVersionNumber() + 1)
                .orElseGet(() -> {
                    if (flat.getCurrentVersionNumber() == null) {
                        return 1;
                    }

                    return flat.getCurrentVersionNumber() + 1;
                });
    }

    private String storeFile(
            MultipartFile file,
            String flatCode,
            Integer versionNumber) {

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

            String newFileName = safeCode
                    + "_V"
                    + versionNumber
                    + "_"
                    + System.currentTimeMillis()
                    + extension;

            Path uploadPath = getVersionUploadPath();

            Files.createDirectories(uploadPath);

            Path targetLocation = uploadPath.resolve(newFileName).normalize();

            Files.copy(
                    file.getInputStream(),
                    targetLocation,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return targetLocation.toString();

        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el archivo de la versión.", e);
        }
    }

    private Path getVersionUploadPath() {
        return Paths.get(versionUploadDir).toAbsolutePath().normalize();
    }

    private String normalize(String value) {

        if (value == null) {
            return null;
        }

        return value.trim();
    }
}