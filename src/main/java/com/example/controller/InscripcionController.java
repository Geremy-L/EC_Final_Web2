package com.example.controller;

import com.example.model.Inscripcion;
import com.example.service.InscripcionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inscripciones")
@RequiredArgsConstructor
public class InscripcionController {

    private final InscripcionService service;

    @GetMapping("/listar")
    public ResponseEntity<List<Inscripcion>> listar() {
        return new ResponseEntity<>(service.listar(), HttpStatus.OK);
    }

    @PostMapping("/inscribir")
    public ResponseEntity<?> inscribir(@RequestParam Long alumnoId, @RequestParam Long cursoId) {
        service.inscribir(alumnoId, cursoId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}