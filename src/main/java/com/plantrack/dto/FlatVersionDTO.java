package com.plantrack.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class FlatVersionDTO {

    private Long id;

    /*
     * No lo cargamos manualmente desde el formulario principal.
     * Se usa para saber a qué plano pertenece la versión.
     */
    private Long flatId;

    /*
     * El número de versión lo calcula automáticamente el servicio.
     */
    private Integer versionNumber;

    @NotBlank(message = "Debe ingresar una descripción del cambio")
    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    private String changeDescription;

    /*
     * Archivo de la nueva versión del plano.
     */
    private MultipartFile file;

    public FlatVersionDTO() {
    }

    // ===========================
    // Getters & Setters
    // ===========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFlatId() {
        return flatId;
    }

    public void setFlatId(Long flatId) {
        this.flatId = flatId;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getChangeDescription() {
        return changeDescription;
    }

    public void setChangeDescription(String changeDescription) {
        this.changeDescription = changeDescription;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}