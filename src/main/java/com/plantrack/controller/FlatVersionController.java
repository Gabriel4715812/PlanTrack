package com.plantrack.controller;

import java.nio.charset.StandardCharsets;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.plantrack.dto.FlatVersionDTO;
import com.plantrack.model.Flat;
import com.plantrack.model.FlatVersion;
import com.plantrack.service.FlatService;
import com.plantrack.service.FlatVersionService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/versions")
public class FlatVersionController {

    private final FlatVersionService flatVersionService;
    private final FlatService flatService;

    public FlatVersionController(
            FlatVersionService flatVersionService,
            FlatService flatService) {

        this.flatVersionService = flatVersionService;
        this.flatService = flatService;
    }

    /*
     * Listado general de versiones
     */
    @GetMapping
    public String list(Model model) {

        model.addAttribute("versions", flatVersionService.findActive());
        model.addAttribute("flat", null);

        return "versions/list";
    }

    /*
     * Listado de versiones de un plano específico
     */
    @GetMapping("/flat/{flatId}")
    public String listByFlat(
            @PathVariable Long flatId,
            Model model) {

        Flat flat = flatService.findById(flatId);

        model.addAttribute("flat", flat);
        model.addAttribute("versions", flatVersionService.findByFlat(flatId));

        return "versions/list";
    }

    /*
     * Formulario para cargar nueva versión de un plano
     */
    @GetMapping("/new/{flatId}")
    public String create(
            @PathVariable Long flatId,
            Model model) {

        Flat flat = flatService.findById(flatId);

        FlatVersionDTO dto = new FlatVersionDTO();
        dto.setFlatId(flatId);

        model.addAttribute("flat", flat);
        model.addAttribute("version", dto);

        return "versions/create";
    }

    /*
     * Guardar nueva versión
     */
    @PostMapping("/save/{flatId}")
public String save(
        @PathVariable Long flatId,
        @Valid @ModelAttribute("version") FlatVersionDTO dto,
        BindingResult result,
        Model model,
        Authentication authentication,
        RedirectAttributes redirectAttributes) {

    Flat flat = flatService.findById(flatId);

    if (result.hasErrors()) {
        model.addAttribute("flat", flat);
        return "versions/create";
    }

    try {
        String username = authentication.getName();

        flatVersionService.createVersion(flatId, dto, username);

        redirectAttributes.addFlashAttribute("success", "Nueva versión registrada correctamente.");

    } catch (RuntimeException e) {

        model.addAttribute("error", e.getMessage());
        model.addAttribute("flat", flat);

        return "versions/create";
    }

    return "redirect:/versions/flat/" + flatId;
}

    /*
     * Detalle de una versión
     */
    @GetMapping("/detail/{id}")
    public String detail(
            @PathVariable Long id,
            Model model) {

        FlatVersion version = flatVersionService.findById(id);

        model.addAttribute("version", version);

        return "versions/detail";
    }

    /*
     * Descargar archivo asociado a una versión
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) {

        FlatVersion version = flatVersionService.findById(id);

        Resource resource = flatVersionService.loadFileAsResource(id);

        String fileName = version.getFileName();

        if (fileName == null || fileName.isBlank()) {
            fileName = "version-" + version.getId();
        }

        String contentType = version.getContentType();

        if (contentType == null || contentType.isBlank()) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        String encodedFileName = java.net.URLEncoder
                .encode(fileName, StandardCharsets.UTF_8)
                .replace("+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename*=UTF-8''" + encodedFileName
                )
                .body(resource);
    }

    /*
     * Visualizar archivo de la versión en el navegador
     */
    @GetMapping("/view/{id}")
    public ResponseEntity<Resource> view(@PathVariable Long id) {

        FlatVersion version = flatVersionService.findById(id);

        Resource resource = flatVersionService.loadFileAsResource(id);

        String contentType = version.getContentType();

        if (contentType == null || contentType.isBlank()) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        String fileName = version.getFileName();

        if (fileName == null || fileName.isBlank()) {
            fileName = "version-" + version.getId();
        }

        String encodedFileName = java.net.URLEncoder
                .encode(fileName, StandardCharsets.UTF_8)
                .replace("+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename*=UTF-8''" + encodedFileName
                )
                .body(resource);
    }
}