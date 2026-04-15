package com.example.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeccionResponse {

    private String titulo;
    private String contenido;
    private Integer orden;
    private CursoResponse curso;
}