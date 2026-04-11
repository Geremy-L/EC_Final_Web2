package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InscripcionResponse {

    private Long id;
    private UsuarioResponse alumno;
    private CursoResponse curso;
    private String estado;
    private LocalDateTime fechaInscripcion;
}