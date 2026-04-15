package com.example.config;

import com.example.model.*;
import com.example.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.util.Arrays;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        seedRoles();
        seedAdmin();
    }

    private void seedRoles() {

        Arrays.asList(
                Rol.NombreRol.ROLE_ADMIN,
                Rol.NombreRol.ROLE_DOCENTE,
                Rol.NombreRol.ROLE_ALUMNO
        ).forEach(rol -> {
            if (!rolRepository.existsByNombre(rol)) {
                rolRepository.save(new Rol(null, rol));
            }
        });
    }

    private void seedAdmin() {
        if (usuarioRepository.count() == 0) {

            Rol adminRol = rolRepository.findByNombre(Rol.NombreRol.ROLE_ADMIN)
                    .orElseThrow();

            if (usuarioRepository.findByEmail("admin@admin.com").isEmpty()) {

                Usuario admin = Usuario.builder()
                        .nombre("Admin")
                        .email("admin@admin.com")
                        .password(passwordEncoder.encode("123456"))
                        .activo(true)
                        .roles(Set.of(adminRol))
                        .build();

                usuarioRepository.save(admin);

                log.info("Admin creado: admin@admin.com / 123456");
            }
        }

    }
}