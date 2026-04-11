package com.example.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inscripciones",
    uniqueConstraints = @UniqueConstraint(columnNames = {"alumno_id", "curso_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alumno_id", nullable = false)
    private Usuario alumno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoInscripcion estado;
    //private EstadoInscripcion estado = EstadoInscripcion.ACTIVO;

    private LocalDateTime fechaInscripcion = LocalDateTime.now();

    public enum EstadoInscripcion {
        ACTIVO, COMPLETADO, CANCELADO
    }

    @PrePersist
    protected void onCreate() {
        if (estado == null) {
            estado = EstadoInscripcion.ACTIVO;
        }
        if (fechaInscripcion == null) {
            fechaInscripcion = LocalDateTime.now();
        }
    }
}