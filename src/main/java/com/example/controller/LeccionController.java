package com.example.controller;

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

    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<Leccion>> listar(@PathVariable Long cursoId) {
        return new ResponseEntity<>(service.listarPorCurso(cursoId), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('DOCENTE','ADMIN')")
    @PostMapping("/agregar/{cursoId}")
    public ResponseEntity<?> agregar(@PathVariable Long cursoId, @RequestBody Leccion leccion) {
        service.agregar(cursoId, leccion);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}