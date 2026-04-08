package com.example.controller;

import com.example.model.Rol;
import com.example.service.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolController {

    private final RolService service;

    @GetMapping("/listar")
    public ResponseEntity<List<Rol>> listar() {
        return new ResponseEntity<>(service.listar(), HttpStatus.OK);
    }
}