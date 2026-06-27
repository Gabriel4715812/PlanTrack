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

import com.plantrack.dto.FlatDTO;
import com.plantrack.model.Flat;
import com.plantrack.service.FlatService;
import com.plantrack.service.PositionService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/flats")
public class FlatController {

    private final FlatService flatService;
    private final PositionService positionService;

    public FlatController(
            FlatService flatService,
            PositionService positionService) {

        this.flatService = flatService;
        this.positionService = positionService;
    }

    /*
     * Listado principal de planos activos
     */
    @GetMapping
    public String list(Model model) {

        model.addAttribute("flats", flatService.findActive());

        return "flats/list";
    }

    /*
     * Formulario para registrar un nuevo plano
     */
    @GetMapping("/new")
    public String create(Model model) {

        model.addAttribute("flat", new FlatDTO());
        model.addAttribute("positions", positionService.findActive());

        return "flats/create";
    }

    /*
     * Guardar nuevo plano
     */
@PostMapping("/save")
public String save(
        @Valid @ModelAttribute("flat") FlatDTO dto,
        BindingResult result,
        Model model,
        Authentication authentication,
        RedirectAttributes redirectAttributes) {

    if (result.hasErrors()) {
        model.addAttribute("positions", positionService.findActive());
        return "flats/create";
    }

    try {
        String username = authentication.getName();

        flatService.save(dto, username);

        redirectAttributes.addFlashAttribute("success", "Plano registrado correctamente.");

    } catch (RuntimeException e) {

        model.addAttribute("error", e.getMessage());
        model.addAttribute("positions", positionService.findActive());

        return "flats/create";
    }

    return "redirect:/flats";
}

    /*
     * Formulario de edición de plano
     */
    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable Long id,
            Model model) {

        Flat flat = flatService.findById(id);

        FlatDTO dto = new FlatDTO();

        dto.setId(flat.getId());
        dto.setCode(flat.getCode());
        dto.setName(flat.getName());
        dto.setDescription(flat.getDescription());

        if (flat.getPosition() != null) {
            dto.setPositionId(flat.getPosition().getId());
        }

        model.addAttribute("flat", dto);
        model.addAttribute("positions", positionService.findActive());
        model.addAttribute("currentFileName", flat.getFileName());

        return "flats/edit";
    }

    /*
     * Actualizar plano existente
     */
    @PostMapping("/update/{id}")
public String update(
        @PathVariable Long id,
        @Valid @ModelAttribute("flat") FlatDTO dto,
        BindingResult result,
        Model model,
        RedirectAttributes redirectAttributes) {

    if (result.hasErrors()) {
        model.addAttribute("positions", positionService.findActive());
        return "flats/edit";
    }

    try {
        flatService.update(id, dto);

        redirectAttributes.addFlashAttribute("success", "Plano actualizado correctamente.");

    } catch (RuntimeException e) {

        model.addAttribute("error", e.getMessage());
        model.addAttribute("positions", positionService.findActive());

        return "flats/edit";
    }

    return "redirect:/flats";
}

    /*
     * Ver detalle del plano
     */
    @GetMapping("/detail/{id}")
    public String detail(
            @PathVariable Long id,
            Model model) {

        Flat flat = flatService.findById(id);

        model.addAttribute("flat", flat);

        return "flats/detail";
    }

    /*
     * Eliminación lógica del plano
     */
@GetMapping("/delete/{id}")
public String delete(
        @PathVariable Long id,
        RedirectAttributes redirectAttributes) {

    flatService.delete(id);

    redirectAttributes.addFlashAttribute("success", "Plano deshabilitado correctamente.");

    return "redirect:/flats";
}

    /*
     * Búsqueda de planos por nombre, código o descripción
     */
    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) String keyword,
            Model model) {

        model.addAttribute("flats", flatService.search(keyword));
        model.addAttribute("keyword", keyword);

        return "flats/list";
    }

    /*
     * Descargar archivo asociado al plano
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) {

        Flat flat = flatService.findById(id);

        Resource resource = flatService.loadFileAsResource(id);

        String fileName = flat.getFileName();

        if (fileName == null || fileName.isBlank()) {
            fileName = "plano-" + flat.getId();
        }

        String contentType = flat.getContentType();

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
 * Visualizar archivo asociado al plano en el navegador
 */
@GetMapping("/view/{id}")
public ResponseEntity<Resource> view(@PathVariable Long id) {

    Flat flat = flatService.findById(id);

    Resource resource = flatService.loadFileAsResource(id);

    String contentType = flat.getContentType();

    if (contentType == null || contentType.isBlank()) {
        contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }

    String fileName = flat.getFileName();

    if (fileName == null || fileName.isBlank()) {
        fileName = "plano-" + flat.getId();
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