package com.plantrack.controller;

import com.plantrack.dto.EquipmentDTO;
import com.plantrack.model.Equipment;
import com.plantrack.service.EquipmentService;
import com.plantrack.service.SectorService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/equipments")
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final SectorService sectorService;

    public EquipmentController(
            EquipmentService equipmentService,
            SectorService sectorService) {

        this.equipmentService = equipmentService;
        this.sectorService = sectorService;
    }

    /**
     * Lista de equipos
     */
    @GetMapping
    public String list(Model model) {

        model.addAttribute("equipments", equipmentService.findActive());

        return "equipments/list";
    }

    /**
     * Formulario nuevo
     */
    @GetMapping("/new")
    public String create(Model model) {

        model.addAttribute("equipment", new EquipmentDTO());

        model.addAttribute("sectors",
                sectorService.findActive());

        return "equipments/create";
    }

    /**
     * Guardar
     */

    @PostMapping("/save")
        public String save(
                @Valid @ModelAttribute("equipment") EquipmentDTO dto,
                BindingResult result,
                Model model,
                RedirectAttributes redirectAttributes) {

            if (result.hasErrors()) {
                model.addAttribute("sectors", sectorService.findActive());
                return "equipments/create";
            }

            try {
                equipmentService.save(dto);
                redirectAttributes.addFlashAttribute("success", "Equipo registrado correctamente.");

            } catch (RuntimeException e) {
                model.addAttribute("error", e.getMessage());
                model.addAttribute("sectors", sectorService.findActive());
                return "equipments/create";
            }

        return "redirect:/equipments";
    }

    /**
     * Formulario editar
     */
    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable Long id,
            Model model) {

        Equipment equipment = equipmentService.findById(id);

        EquipmentDTO dto = new EquipmentDTO();

        dto.setId(equipment.getId());
        dto.setName(equipment.getName());
        dto.setCode(equipment.getCode());
        dto.setDescription(equipment.getDescription());
        dto.setSectorId(equipment.getSector().getId());

        model.addAttribute("equipment", dto);

        model.addAttribute("sectors",
                sectorService.findActive());

        return "equipments/edit";
    }

    /**
     * Actualizar
     */
        @PostMapping("/update/{id}")
        public String update(
                @PathVariable Long id,
                @Valid @ModelAttribute("equipment") EquipmentDTO dto,
                BindingResult result,
                Model model,
                RedirectAttributes redirectAttributes) {

            if (result.hasErrors()) {
                model.addAttribute("sectors", sectorService.findActive());
                return "equipments/edit";
            }

            try {
                equipmentService.update(id, dto);
                redirectAttributes.addFlashAttribute("success", "Equipo actualizado correctamente.");

            } catch (RuntimeException e) {
                model.addAttribute("error", e.getMessage());
                model.addAttribute("sectors", sectorService.findActive());
                return "equipments/edit";
            }

            return "redirect:/equipments";
        }

    /**
     * Eliminación lógica
     */
    @GetMapping("/delete/{id}")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        equipmentService.delete(id);

        redirectAttributes.addFlashAttribute("success", "Equipo deshabilitado correctamente.");

        return "redirect:/equipments";
    }

    /**
     * Buscar
     */
    @GetMapping("/search")
    public String search(
            @RequestParam String name,
            Model model) {

        model.addAttribute(
                "equipments",
                equipmentService.search(name));

        return "equipments/list";
    }

}