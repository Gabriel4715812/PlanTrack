package com.plantrack.service;

import com.plantrack.dto.UserForm;
import com.plantrack.model.Role;
import com.plantrack.model.User;
import com.plantrack.repository.RoleRepository;
import com.plantrack.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
    }

    public UserForm getFormById(Long id) {
        User user = findById(id);

        UserForm form = new UserForm();
        form.setId(user.getId());
        form.setFirstName(user.getFirstName());
        form.setLastName(user.getLastName());
        form.setUsername(user.getUsername());
        form.setEmail(user.getEmail());
        form.setPhone(user.getPhone());
        form.setDocket(user.getDocket());
        form.setEnabled(user.getEnabled());
        form.setRoleId(user.getRole().getId());

        // No cargamos password por seguridad
        return form;
    }

    public void save(UserForm form) {
        Role role = roleRepository.findById(form.getRoleId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        User user = new User();
        user.setFirstName(form.getFirstName());
        user.setLastName(form.getLastName());
        user.setUsername(form.getUsername());
        user.setEmail(form.getEmail());
        user.setPhone(form.getPhone());
        user.setDocket(form.getDocket());
        user.setEnabled(form.getEnabled() != null ? form.getEnabled() : true);
        user.setRole(role);

        // contraseña obligatoria al crear
        user.setPasswordHash(passwordEncoder.encode(form.getPassword()));

        userRepository.save(user);
    }
public void registerPublicUser(UserForm form) {

    if (form.getUsername() == null || form.getUsername().isBlank()) {
        throw new RuntimeException("El nombre de usuario es obligatorio.");
    }

    if (form.getPassword() == null || form.getPassword().isBlank()) {
        throw new RuntimeException("La contraseña es obligatoria.");
    }

    if (userRepository.findByUsername(form.getUsername()).isPresent()) {
        throw new RuntimeException("Ya existe un usuario registrado con ese nombre de usuario.");
    }

    Role operatorRole = roleRepository.findByName("OPERATOR")
            .orElseThrow(() -> new RuntimeException("No se encontró el rol OPERATOR."));

    User user = new User();

    user.setFirstName(form.getFirstName());
    user.setLastName(form.getLastName());
    user.setUsername(form.getUsername());
    user.setEmail(form.getEmail());
    user.setPhone(form.getPhone());
    user.setDocket(form.getDocket());
    user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
    user.setEnabled(true);
    user.setRole(operatorRole);

    userRepository.save(user);
}
    public void update(UserForm form) {
        User user = findById(form.getId());

        Role role = roleRepository.findById(form.getRoleId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        user.setFirstName(form.getFirstName());
        user.setLastName(form.getLastName());
        user.setUsername(form.getUsername());
        user.setEmail(form.getEmail());
        user.setPhone(form.getPhone());
        user.setDocket(form.getDocket());
        user.setEnabled(form.getEnabled() != null ? form.getEnabled() : true);
        user.setRole(role);

        // solo actualiza contraseña si el admin ingresó una nueva
        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        }

        userRepository.save(user);
    }

    public void toggleStatus(Long id) {
        User user = findById(id);
        user.setEnabled(!user.getEnabled());
        userRepository.save(user);
    }
}