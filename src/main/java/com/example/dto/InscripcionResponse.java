package com.example.dto;

import lombok.*;
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
    private Double nota;
    private LocalDateTime fechaInscripcion;
}
