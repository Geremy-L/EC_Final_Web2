package com.example.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CertificadoResponse {

    private Long id;
    private String codigo;
    private LocalDateTime emitidoEn;
    private UsuarioResponse alumno;
    private CursoResponse curso;
}
