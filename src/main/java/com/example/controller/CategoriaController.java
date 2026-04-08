package com.example.controller;

import com.example.model.Categoria;
import com.example.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService service;

    @GetMapping("/listar")
    public ResponseEntity<List<Categoria>> listar() {
        return new ResponseEntity<>(service.listar(), HttpStatus.OK);
    }

    @PostMapping("/agregar")
    public ResponseEntity<?> agregar(@RequestBody Categoria c) {
        service.agregar(c);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}