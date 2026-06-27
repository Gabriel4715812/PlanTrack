package com.plantrack.controller;

import com.plantrack.dto.UserForm;
import com.plantrack.service.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new UserForm());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute("user") UserForm form,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            userService.registerPublicUser(form);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Usuario registrado correctamente. Ya puede iniciar sesión."
            );

            return "redirect:/login";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", form);
            return "register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}