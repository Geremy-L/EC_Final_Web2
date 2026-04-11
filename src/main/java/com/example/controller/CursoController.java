package com.example.controller;

import com.example.dto.CursoResponse;
import com.example.model.Curso;
import com.example.service.CursoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService service;

    @GetMapping("/listar")
    public ResponseEntity<List<CursoResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<CursoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @PreAuthorize("hasRole('ADMIN', 'DOCENTE')")
    @PostMapping("/crear")
    public ResponseEntity<CursoResponse> crear(@RequestBody Curso curso) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(curso));
    }

    @PutMapping("/actualizar/{id}")
    @PreAuthorize("hasRole('ADMIN', 'DOCENTE')")
    public ResponseEntity<CursoResponse> actualizar(@PathVariable Long id,
                                                    @RequestBody Curso curso) {
        return ResponseEntity.ok(service.actualizar(id, curso));
    }

    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE')")
    @PutMapping("/desactivar/{id}")
    public ResponseEntity<CursoResponse> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(service.desactivar(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE')")
    @PutMapping("/activar/{id}")
    public ResponseEntity<CursoResponse> activar(@PathVariable Long id) {
        return ResponseEntity.ok(service.activar(id));
    }
}