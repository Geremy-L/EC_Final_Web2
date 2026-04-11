package com.example.controller;

import com.example.model.Categoria;
import com.example.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService service;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/listar")
    public ResponseEntity<List<Categoria>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Categoria> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/agregar")
    public ResponseEntity<Categoria> agregar(@RequestBody Categoria c) {
        Categoria nueva = service.agregar(c);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/editar/{id}")
    public ResponseEntity<Categoria> editar(@PathVariable Long id, @RequestBody Categoria c) {
        return ResponseEntity.ok(service.editar(id, c));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok().build();
    }
}