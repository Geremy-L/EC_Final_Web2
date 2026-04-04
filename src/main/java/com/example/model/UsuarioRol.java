package com.example.model;

import jakarta.persistence.*;
import lombok.*;

// Tabla intermedia para la relación ManyToMany Usuario <-> Rol
// JPA la maneja automáticamente, pero si necesitas atributos extra:
@Entity
@Table(name = "usuario_roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "rol_id")
    private Rol rol;
}