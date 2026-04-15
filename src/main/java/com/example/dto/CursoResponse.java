package com.example.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CursoResponse {

    private Long id;
    private String titulo;
    private String descripcion;
    private Boolean activo;
    private LocalDateTime creadoEn;

    private UsuarioResponse docente;
}