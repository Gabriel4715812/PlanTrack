package com.plantrack.controller;

import com.plantrack.dto.PositionDTO;
import com.plantrack.model.Position;
import com.plantrack.service.PartService;
import com.plantrack.service.PositionService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/positions")
public class PositionController {

    private final PositionService positionService;
    private final PartService partService;

    public PositionController(
            PositionService positionService,
            PartService partService) {

        this.positionService = positionService;
        this.partService = partService;
    }

    @GetMapping
    public String list(Model model) {

        model.addAttribute("positions", positionService.findActive());

        return "positions/list";
    }

    @GetMapping("/new")
    public String create(Model model) {

        model.addAttribute("position", new PositionDTO());
        model.addAttribute("parts", partService.findActive());

        return "positions/create";
    }

    @PostMapping("/save")
public String save(
        @Valid @ModelAttribute("position") PositionDTO dto,
        BindingResult result,
        Model model,
        RedirectAttributes redirectAttributes) {

    if (result.hasErrors()) {
        model.addAttribute("parts", partService.findActive());
        return "positions/create";
    }

    try {
        positionService.save(dto);
        redirectAttributes.addFlashAttribute("success", "Posición registrada correctamente.");

    } catch (RuntimeException e) {
        model.addAttribute("error", e.getMessage());
        model.addAttribute("parts", partService.findActive());
        return "positions/create";
    }

    return "redirect:/positions";
}

    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable Long id,
            Model model) {

        Position position = positionService.findById(id);

        PositionDTO dto = new PositionDTO();

        dto.setId(position.getId());
        dto.setName(position.getName());
        dto.setCode(position.getCode());
        dto.setDescription(position.getDescription());
        dto.setPartId(position.getPart().getId());

        model.addAttribute("position", dto);
        model.addAttribute("parts", partService.findActive());

        return "positions/edit";
    }

@PostMapping("/update/{id}")
public String update(
        @PathVariable Long id,
        @Valid @ModelAttribute("position") PositionDTO dto,
        BindingResult result,
        Model model,
        RedirectAttributes redirectAttributes) {

    if (result.hasErrors()) {
        model.addAttribute("parts", partService.findActive());
        return "positions/edit";
    }

    try {
        positionService.update(id, dto);
        redirectAttributes.addFlashAttribute("success", "Posición actualizada correctamente.");

    } catch (RuntimeException e) {
        model.addAttribute("error", e.getMessage());
        model.addAttribute("parts", partService.findActive());
        return "positions/edit";
    }

    return "redirect:/positions";
}

@GetMapping("/delete/{id}")
public String delete(
        @PathVariable Long id,
        RedirectAttributes redirectAttributes) {

    positionService.delete(id);

    redirectAttributes.addFlashAttribute("success", "Posición deshabilitada correctamente.");

    return "redirect:/positions";
}

    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) String name,
            Model model) {

        model.addAttribute("positions", positionService.search(name));
        model.addAttribute("keyword", name);

        return "positions/list";
    }
}