package com.example.controller;

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
    public ResponseEntity<List<Curso>> listar() {
        return new ResponseEntity<>(service.listar(), HttpStatus.OK);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        return new ResponseEntity<>(service.buscar(id), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('DOCENTE','ADMIN')")
    @PostMapping("/agregar")
    public ResponseEntity<?> agregar(@RequestBody Curso curso) {
        service.agregar(curso);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('DOCENTE','ADMIN')")
    @PutMapping("/editar/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody Curso curso) {
        service.editar(id, curso);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('DOCENTE','ADMIN')")
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}