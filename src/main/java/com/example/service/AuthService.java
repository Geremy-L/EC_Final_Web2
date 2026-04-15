package com.example.service;

import com.example.dto.AuthResponse;
import com.example.dto.LoginRequest;
import com.example.dto.RegisterRequest;
import com.example.exception.BadRequestException;
import com.example.exception.ResourceNotFoundException;
import com.example.model.Rol;
import com.example.model.Usuario;
import com.example.repository.RolRepository;
import com.example.repository.UsuarioRepository;
import com.example.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse login(LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

            List<String> roles = usuario.getRoles().stream()
                    .map(r -> r.getNombre().name())
                    .toList();

            return AuthResponse.builder()
                    .token(token)
                    .tipo("Bearer")
                    .email(usuario.getEmail())
                    .nombre(usuario.getNombre())
                    .roles(roles)
                    .build();

        } catch (AuthenticationException e) {
            throw new BadRequestException("Email o contraseña incorrectos");
        }
    }

    public AuthResponse register(RegisterRequest request) {

        // Verificar si el email ya existe
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("El email ya está registrado");
        }

        // Por defecto asignar ROLE_ALUMNO
        Rol rol = rolRepository.findByNombre(Rol.NombreRol.ROLE_ALUMNO)
                .orElseThrow(() -> new ResourceNotFoundException("Rol ALUMNO no encontrado"));

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .activo(true)
                .roles(Set.of(rol))
                .build();

        usuarioRepository.save(usuario);

        // Generar token automáticamente tras el registro
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(usuario.getEmail())
                .password(usuario.getPassword())
                .authorities("ROLE_ALUMNO")
                .build();

        String token = jwtUtil.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .tipo("Bearer")
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .roles(List.of("ROLE_ALUMNO"))
                .build();
    }
}
