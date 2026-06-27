package com.plantrack.config;

import com.plantrack.model.Role;
import com.plantrack.model.User;
import com.plantrack.repository.RoleRepository;
import com.plantrack.repository.UserRepository;


@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void run(String... args) throws Exception {

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    Role role = new Role(null, "ADMIN");
                    roleRepository.save(role);
                    return role;
                });

        roleRepository.findByName("DESIGNER")
                .orElseGet(() -> {
                    Role role = new Role(null, "DESIGNER");
                    roleRepository.save(role);
                    return role;
                });

        roleRepository.findByName("OPERATOR")
                .orElseGet(() -> {
                    Role role = new Role(null, "OPERATOR");
                    roleRepository.save(role);
                    return role;
                });

        userRepository.findByUsername("admin").orElseGet(() -> {
            User admin = new User();
            admin.setFirstName("Administrador");
            admin.setLastName("PlanTrack");
            admin.setUsername("admin");
            admin.setEmail("admin@plantrack.com");
            admin.setPhone("000000000");
            admin.setDocket("ADM-001");
            admin.setPasswordHash(passwordEncoder.encode("Admin1234"));
            admin.setEnabled(true);
            admin.setRole(adminRole);

            return userRepository.save(admin);
        });
    }
}
