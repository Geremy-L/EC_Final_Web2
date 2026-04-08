package com.example.service;

import com.example.model.*;
import com.example.repository.*;
import com.example.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Usuario> listar() {
        return repository.findAll();
    }

    public Usuario buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    public Usuario guardar(Usuario usuario) {
        return repository.save(usuario);
    }

    // ESTE es el importante
    public Usuario crearConRol(String nombre, String email, String password, Rol.NombreRol rolNombre) {

        Rol rol = rolRepository.findByNombre(rolNombre)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));

        Usuario usuario = Usuario.builder()
                .nombre(nombre)
                .email(email)
                .password(passwordEncoder.encode(password))
                .roles(Set.of(rol)) // 👈 aquí se asigna el rol
                .build();

        return repository.save(usuario);
    }

    // Desactivar usuario
    public Usuario desactivar(Long id) {
        Usuario usuario = buscar(id);
        usuario.setActivo(false);
        return repository.save(usuario);
    }

    // Activar usuario
    public Usuario activar(Long id) {
        Usuario usuario = buscar(id);
        usuario.setActivo(true);
        return repository.save(usuario);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}