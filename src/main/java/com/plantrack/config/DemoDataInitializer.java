package com.plantrack.config;

import com.plantrack.model.Equipment;
import com.plantrack.model.Part;
import com.plantrack.model.Position;
import com.plantrack.model.Role;
import com.plantrack.model.Sector;
import com.plantrack.model.User;

import com.plantrack.repository.EquipmentRepository;
import com.plantrack.repository.PartRepository;
import com.plantrack.repository.PositionRepository;
import com.plantrack.repository.RoleRepository;
import com.plantrack.repository.SectorRepository;
import com.plantrack.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DemoDataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final SectorRepository sectorRepository;
    private final EquipmentRepository equipmentRepository;
    private final PartRepository partRepository;
    private final PositionRepository positionRepository;
    private final PasswordEncoder passwordEncoder;

    public DemoDataInitializer(
            RoleRepository roleRepository,
            UserRepository userRepository,
            SectorRepository sectorRepository,
            EquipmentRepository equipmentRepository,
            PartRepository partRepository,
            PositionRepository positionRepository,
            PasswordEncoder passwordEncoder) {

        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.sectorRepository = sectorRepository;
        this.equipmentRepository = equipmentRepository;
        this.partRepository = partRepository;
        this.positionRepository = positionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        Role adminRole = createRoleIfNotExists("ADMIN");
        Role designerRole = createRoleIfNotExists("DESIGNER");
        Role operatorRole = createRoleIfNotExists("OPERATOR");

        createUserIfNotExists(
                "admin",
                "Administrador",
                "PlanTrack",
                "admin@plantrack.com",
                "000000000",
                "ADM-001",
                "Admin1234",
                adminRole
        );

        createUserIfNotExists(
                "designer",
                "Diseñador",
                "Demo",
                "designer@plantrack.com",
                "000000001",
                "DES-001",
                "Demo1234",
                designerRole
        );

        createUserIfNotExists(
                "operator",
                "Operario",
                "Demo",
                "operator@plantrack.com",
                "000000002",
                "OPE-001",
                "Demo1234",
                operatorRole
        );

        Sector sectorTurron = createSectorIfNotExists("001", "Turrón");
        Sector sectorChicle = createSectorIfNotExists("002", "Chicle");

        Equipment envolvedora = createEquipmentIfNotExists(
                1001,
                "Envolvedora Theegarten",
                "Equipo utilizado para el proceso de envoltura de productos.",
                sectorTurron
        );

        Equipment formadora = createEquipmentIfNotExists(
                1002,
                "Formadora de barras",
                "Equipo utilizado para el formado de barras de producto.",
                sectorChicle
        );

        Part mordaza = createPartIfNotExists(
                2001,
                "Mordaza de sellado",
                "Componente utilizado para el sellado del envoltorio.",
                envolvedora
        );

        Part ejePrincipal = createPartIfNotExists(
                2002,
                "Eje principal",
                "Elemento mecánico principal de transmisión.",
                formadora
        );

        createPositionIfNotExists(
                3001,
                "Lado operador",
                "Ubicación correspondiente al lado de operación de la máquina.",
                mordaza
        );

        createPositionIfNotExists(
                3002,
                "Lado transmisión",
                "Ubicación correspondiente al lado de transmisión del equipo.",
                ejePrincipal
        );

        System.out.println("Datos demo de PlanTrack cargados correctamente.");
        System.out.println("Usuarios disponibles:");
        System.out.println("admin / Admin1234");
        System.out.println("designer / Demo1234");
        System.out.println("operator / Demo1234");
    }

    private Role createRoleIfNotExists(String name) {

        return roleRepository.findByName(name)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(name);
                    return roleRepository.save(role);
                });
    }

    private void createUserIfNotExists(
            String username,
            String firstName,
            String lastName,
            String email,
            String phone,
            String docket,
            String password,
            Role role) {

        if (userRepository.findByUsername(username).isPresent()) {
            return;
        }

        User user = new User();

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setPhone(phone);
        user.setDocket(docket);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setEnabled(true);
        user.setRole(role);

        userRepository.save(user);
    }

    private Sector createSectorIfNotExists(
            String code,
            String name) {

        return sectorRepository.findByCode(code)
                .orElseGet(() -> {
                    Sector sector = new Sector();
                    sector.setCode(code);
                    sector.setName(name);
                    sector.setActive(true);
                    return sectorRepository.save(sector);
                });
    }

    private Equipment createEquipmentIfNotExists(
            Integer code,
            String name,
            String description,
            Sector sector) {

        return equipmentRepository.findByCode(code)
                .orElseGet(() -> {
                    Equipment equipment = new Equipment();
                    equipment.setCode(code);
                    equipment.setName(name);
                    equipment.setDescription(description);
                    equipment.setSector(sector);
                    equipment.setActive(true);
                    return equipmentRepository.save(equipment);
                });
    }

    private Part createPartIfNotExists(
            Integer code,
            String name,
            String description,
            Equipment equipment) {

        return partRepository.findByCode(code)
                .orElseGet(() -> {
                    Part part = new Part();
                    part.setCode(code);
                    part.setName(name);
                    part.setDescription(description);
                    part.setEquipment(equipment);
                    part.setActive(true);
                    return partRepository.save(part);
                });
    }

    private Position createPositionIfNotExists(
            Integer code,
            String name,
            String description,
            Part part) {

        return positionRepository.findByCode(code)
                .orElseGet(() -> {
                    Position position = new Position();
                    position.setCode(code);
                    position.setName(name);
                    position.setDescription(description);
                    position.setPart(part);
                    position.setActive(true);
                    return positionRepository.save(position);
                });
    }
}
