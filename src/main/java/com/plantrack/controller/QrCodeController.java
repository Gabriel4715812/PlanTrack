package com.plantrack.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.plantrack.model.QrCode;
import com.plantrack.service.FlatService;
import com.plantrack.service.QrCodeService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/qr")
public class QrCodeController {

    private final QrCodeService qrCodeService;
    private final FlatService flatService;

    public QrCodeController(
            QrCodeService qrCodeService,
            FlatService flatService) {

        this.qrCodeService = qrCodeService;
        this.flatService = flatService;
    }

    /*
     * Listado general de códigos QR generados.
     */
    @GetMapping
    public String list(Model model) {

        model.addAttribute("qrCodes", qrCodeService.findActive());

        return "qr/list";
    }

    /*
     * Ver QR asociado a un plano específico.
     */
    @GetMapping("/flat/{flatId}")
    public String findByFlat(
            @PathVariable Long flatId,
            Model model) {

        QrCode qrCode = qrCodeService.findByFlat(flatId);

        model.addAttribute("qrCode", qrCode);

        return "qr/detail";
    }

    /*
     * Generar QR para un plano.
     */
@PostMapping("/generate/{flatId}")
public String generate(
        @PathVariable Long flatId,
        HttpServletRequest request,
        RedirectAttributes redirectAttributes) {

    String baseUrl = getBaseUrl(request);

    QrCode qrCode = qrCodeService.generateForFlat(flatId, baseUrl);

    redirectAttributes.addFlashAttribute("success", "Código QR generado correctamente.");

    return "redirect:/qr/detail/" + qrCode.getId();
}

    /*
     * Detalle de un QR generado.
     */
    @GetMapping("/detail/{id}")
    public String detail(
            @PathVariable Long id,
            Model model) {

        QrCode qrCode = qrCodeService.findById(id);

        model.addAttribute("qrCode", qrCode);

        return "qr/detail";
    }

    /*
     * Mostrar imagen QR en el navegador.
     */
    @GetMapping("/image/{id}")
    public ResponseEntity<Resource> image(@PathVariable Long id) {

        Resource resource = qrCodeService.loadQrImageAsResource(id);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"qr-plantrack.png\""
                )
                .body(resource);
    }

    /*
     * Acceso rápido al plano desde el QR.
     * Esta ruta puede usarse si luego querés que el QR apunte a /qr/access/{id}.
     * Por ahora el QR apunta directamente a /flats/detail/{id}.
     */
    @GetMapping("/access/{flatId}")
    public String accessFlat(@PathVariable Long flatId) {

        flatService.findById(flatId);

        return "redirect:/flats/detail/" + flatId;
    }

    private String getBaseUrl(HttpServletRequest request) {

        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();

        boolean defaultHttp = scheme.equals("http") && serverPort == 80;
        boolean defaultHttps = scheme.equals("https") && serverPort == 443;

        if (defaultHttp || defaultHttps) {
            return scheme + "://" + serverName;
        }

        return scheme + "://" + serverName + ":" + serverPort;
    }
}