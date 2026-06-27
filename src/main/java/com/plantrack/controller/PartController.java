package com.plantrack.controller;

import com.plantrack.dto.PartDTO;
import com.plantrack.model.Part;
import com.plantrack.service.EquipmentService;
import com.plantrack.service.PartService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/parts")
public class PartController {

    private final PartService partService;
    private final EquipmentService equipmentService;

    public PartController(
            PartService partService,
            EquipmentService equipmentService) {

        this.partService = partService;
        this.equipmentService = equipmentService;
    }

    @GetMapping
    public String list(Model model) {

        model.addAttribute("parts", partService.findActive());

        return "parts/list";
    }

    @GetMapping("/new")
    public String create(Model model) {

        model.addAttribute("part", new PartDTO());
        model.addAttribute("equipments", equipmentService.findActive());

        return "parts/create";
    }

        @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("part") PartDTO dto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("equipments", equipmentService.findActive());
            return "parts/create";
        }

        try {
            partService.save(dto);
            redirectAttributes.addFlashAttribute("success", "Pieza registrada correctamente.");

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("equipments", equipmentService.findActive());
            return "parts/create";
        }

    return "redirect:/parts";
}


    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable Long id,
            Model model) {

        Part part = partService.findById(id);

        PartDTO dto = new PartDTO();

        dto.setId(part.getId());
        dto.setName(part.getName());
        dto.setCode(part.getCode());
        dto.setDescription(part.getDescription());
        dto.setEquipmentId(part.getEquipment().getId());

        model.addAttribute("part", dto);
        model.addAttribute("equipments", equipmentService.findActive());

        return "parts/edit";
    }

    @PostMapping("/update/{id}")
public String update(
        @PathVariable Long id,
        @Valid @ModelAttribute("part") PartDTO dto,
        BindingResult result,
        Model model,
        RedirectAttributes redirectAttributes) {

    if (result.hasErrors()) {
        model.addAttribute("equipments", equipmentService.findActive());
        return "parts/edit";
    }

    try {
        partService.update(id, dto);
        redirectAttributes.addFlashAttribute("success", "Pieza actualizada correctamente.");

    } catch (RuntimeException e) {
        model.addAttribute("error", e.getMessage());
        model.addAttribute("equipments", equipmentService.findActive());
        return "parts/edit";
    }

    return "redirect:/parts";
}

   @GetMapping("/delete/{id}")
public String delete(
        @PathVariable Long id,
        RedirectAttributes redirectAttributes) {

    partService.delete(id);

    redirectAttributes.addFlashAttribute("success", "Pieza deshabilitada correctamente.");

    return "redirect:/parts";
}

    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) String name,
            Model model) {

        model.addAttribute("parts", partService.search(name));
        model.addAttribute("keyword", name);

        return "parts/list";
    }
}