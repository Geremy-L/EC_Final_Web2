package com.example.service;

import com.example.dto.UsuarioResponse;
import com.example.model.*;
import com.example.repository.*;
import com.example.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UsuarioResponse> listar() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UsuarioResponse buscar(Long id) {
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado")));
    }

    public UsuarioResponse crear(String nombre, String email, String password, List<String> rolesRequest) {

        Set<Rol> roles = rolesRequest.stream()
                .map(r -> rolRepository.findByNombre(Rol.NombreRol.valueOf(r))
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + r)))
                .collect(java.util.stream.Collectors.toSet());

        Usuario usuario = Usuario.builder()
                .nombre(nombre)
                .email(email)
                .password(passwordEncoder.encode(password))
                .activo(true)
                .roles(roles)
                .build();

        return toResponse(repository.save(usuario));
    }

    public UsuarioResponse actualizar(Long id, Usuario datos) {

        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (datos.getNombre() != null) {
            usuario.setNombre(datos.getNombre());
        }

        if (datos.getEmail() != null) {
            usuario.setEmail(datos.getEmail());
        }

        if (datos.getPassword() != null && !datos.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(datos.getPassword()));
        }

        // ESTE BLOQUE ES EL ARREGLO REAL
        if (datos.getRoles() != null) {
            usuario.setRoles(datos.getRoles());
        } else {
            // evita que Hibernate intente meter null
            usuario.setRoles(usuario.getRoles());
        }

        return toResponse(repository.save(usuario));
    }

    public UsuarioResponse desactivar(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        usuario.setActivo(false);
        return toResponse(repository.save(usuario));
    }

    public UsuarioResponse activar(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        usuario.setActivo(true);
        return toResponse(repository.save(usuario));
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }
        repository.deleteById(id);
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .activo(usuario.getActivo()) // is --> get
                .roles(
                        usuario.getRoles().stream()
                                .map(r -> r.getNombre().name())
                                .toList()
                )
                .build();
    }

}