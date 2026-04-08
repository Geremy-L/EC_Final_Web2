package com.example.service;

import com.example.dto.AuthResponse;
import com.example.dto.LoginRequest;
import com.example.model.Usuario;
import com.example.repository.UsuarioRepository;
import com.example.security.JwtUtil;
import com.example.exception.ResourceNotFoundException;
import com.example.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
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

            String token = jwtUtil.generateToken(userDetails); // cambio ROLES

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
}