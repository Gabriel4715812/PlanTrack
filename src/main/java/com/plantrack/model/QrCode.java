package com.plantrack.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "qr_codes")
public class QrCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Cada plano tendrá un único código QR asociado.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flat_id", nullable = false, unique = true)
    private Flat flat;

    /*
     * Valor interno del QR.
     * Puede ser la URL del plano o una ruta del sistema.
     */
    @Column(name = "qr_value", nullable = false, length = 500)
    private String qrValue;

    /*
     * Ruta física donde se guarda la imagen PNG del QR.
     */
    @Column(name = "qr_image_path", length = 500)
    private String qrImagePath;

    /*
     * URL final a la que apunta el QR.
     * Ejemplo: http://localhost:8080/flats/detail/1
     */
    @Column(name = "target_url", length = 500)
    private String targetUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean active = true;

    public QrCode() {
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();

        if (this.active == null) {
            this.active = true;
        }
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

    public Flat getFlat() {
        return flat;
    }

    public void setFlat(Flat flat) {
        this.flat = flat;
    }

    public String getQrValue() {
        return qrValue;
    }

    public void setQrValue(String qrValue) {
        this.qrValue = qrValue;
    }

    public String getQrImagePath() {
        return qrImagePath;
    }

    public void setQrImagePath(String qrImagePath) {
        this.qrImagePath = qrImagePath;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}