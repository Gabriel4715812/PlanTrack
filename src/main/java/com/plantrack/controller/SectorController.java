package com.plantrack.controller;

import com.plantrack.dto.SectorDTO;
import com.plantrack.model.Sector;
import com.plantrack.service.SectorService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sectors")
public class SectorController {

    private final SectorService sectorService;

    public SectorController(SectorService sectorService) {
        this.sectorService = sectorService;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            Model model) {

        model.addAttribute("sectors", sectorService.search(keyword));
        model.addAttribute("keyword", keyword);

        return "sectors/list";
    }

    @GetMapping("/new")
    public String create(Model model) {

        model.addAttribute("sector", new SectorDTO());

        return "sectors/create";
    }

    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("sector") SectorDTO dto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "sectors/create";
        }

        try {
            sectorService.save(dto);
            redirectAttributes.addFlashAttribute("success", "Sector registrado correctamente.");

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "sectors/create";
        }

        return "redirect:/sectors";
    }

    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable Long id,
            Model model) {

        Sector sector = sectorService.findById(id);

        SectorDTO dto = new SectorDTO();
        dto.setId(sector.getId());
        dto.setName(sector.getName());
        dto.setCode(sector.getCode());

        model.addAttribute("sector", dto);

        return "sectors/edit";
    }

    @PostMapping("/update/{id}")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("sector") SectorDTO dto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "sectors/edit";
        }

        try {
            sectorService.update(id, dto);
            redirectAttributes.addFlashAttribute("success", "Sector actualizado correctamente.");

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "sectors/edit";
        }

        return "redirect:/sectors";
    }

    @GetMapping("/delete/{id}")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        sectorService.delete(id);

        redirectAttributes.addFlashAttribute("success", "Sector deshabilitado correctamente.");

        return "redirect:/sectors";
    }
}