package com.plantrack.controller;

import com.plantrack.dto.UserForm;
import com.plantrack.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // LISTADO
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users/list";
    }

    // FORMULARIO NUEVO
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        UserForm form = new UserForm();
        form.setEnabled(true);

        model.addAttribute("userForm", form);
        model.addAttribute("roles", userService.findAllRoles());
        model.addAttribute("isEdit", false);

        return "users/form";
    }

    // GUARDAR NUEVO
@PostMapping("/save")
public String saveUser(@Valid @ModelAttribute("userForm") UserForm form,
                       BindingResult result,
                       Model model) {

    if (form.getPassword() == null || form.getPassword().isBlank()) {
        result.rejectValue("password", "error.userForm", "La contraseña es obligatoria");
    } else if (form.getPassword().length() < 6) {
        result.rejectValue("password", "error.userForm", "La contraseña debe tener al menos 6 caracteres");
    }

    if (result.hasErrors()) {
        model.addAttribute("roles", userService.findAllRoles());
        model.addAttribute("isEdit", false);
        return "users/form";
    }

    userService.save(form);
    return "redirect:/users";
}

    // FORMULARIO EDITAR
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        UserForm form = userService.getFormById(id);

        model.addAttribute("userForm", form);
        model.addAttribute("roles", userService.findAllRoles());
        model.addAttribute("isEdit", true);

        return "users/form";
    }

    // ACTUALIZAR
@PostMapping("/update")
public String updateUser(@Valid @ModelAttribute("userForm") UserForm form,
                         BindingResult result,
                         Model model) {

    if (form.getPassword() != null && !form.getPassword().isBlank() && form.getPassword().length() < 6) {
        result.rejectValue("password", "error.userForm", "La contraseña debe tener al menos 6 caracteres");
    }

    if (result.hasErrors()) {
        model.addAttribute("roles", userService.findAllRoles());
        model.addAttribute("isEdit", true);
        return "users/form";
    }

    userService.update(form);
    return "redirect:/users";
}
}