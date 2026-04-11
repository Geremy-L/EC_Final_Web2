package com.example.controller;

import com.example.dto.LeccionResponse;
import com.example.model.Leccion;
import com.example.service.LeccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lecciones")
@RequiredArgsConstructor
public class LeccionController {

    private final LeccionService service;

    @GetMapping("/listar")
    public ResponseEntity<List<LeccionResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<LeccionResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<LeccionResponse>> listarPorCurso(@PathVariable Long cursoId) {
        return ResponseEntity.ok(service.listarPorCurso(cursoId));
    }

    @PostMapping("/crear")
    public ResponseEntity<LeccionResponse> crear(@RequestBody Leccion leccion) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(leccion));
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<LeccionResponse> actualizar(@PathVariable Long id,
                                                      @RequestBody Leccion leccion) {
        return ResponseEntity.ok(service.actualizar(id, leccion));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}