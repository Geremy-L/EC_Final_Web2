package com.example.controller;

import com.example.dto.InscripcionRequest;
import com.example.dto.InscripcionResponse;
import com.example.model.Inscripcion;
import com.example.service.InscripcionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inscripciones")
@RequiredArgsConstructor
public class InscripcionController {

    private final InscripcionService service;

    @GetMapping("/listar")
    public ResponseEntity<List<InscripcionResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<InscripcionResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @PostMapping("/crear")
    public ResponseEntity<InscripcionResponse> crear(@RequestBody InscripcionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.crear(req));
    }

    @PutMapping("/completar/{id}")
    public ResponseEntity<InscripcionResponse> completar(@PathVariable Long id) {
        return ResponseEntity.ok(service.actualizarEstado(id, "COMPLETADO"));
    }

    @PutMapping("/cancelar/{id}")
    public ResponseEntity<InscripcionResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(service.actualizarEstado(id, "CANCELADO"));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}