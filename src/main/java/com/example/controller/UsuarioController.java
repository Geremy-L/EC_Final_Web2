package com.example.controller;

import com.example.dto.RegisterRequest;
import com.example.model.Rol;
import com.example.model.Usuario;
import com.example.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    // Listar usuarios
    @GetMapping("/listar")
    public ResponseEntity<List<Usuario>> listar() {
        return new ResponseEntity<>(service.listar(), HttpStatus.OK);
    }

    // Mantienes este (pero no asigna roles automáticamente)
    @PostMapping("/agregar")
    public ResponseEntity<?> agregar(@RequestBody Usuario usuario) {
        service.guardar(usuario);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // ESTE es el importante (crear usuario con rol)
    @PostMapping("/crear")
    public ResponseEntity<?> crear(@RequestBody RegisterRequest req) {

        service.crearConRol(
                req.getNombre(),
                req.getEmail(),
                req.getPassword(),
                Rol.NombreRol.valueOf(req.getRol())
        );

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Buscar usuario por id
    @GetMapping("/buscar/{id}")
    public ResponseEntity<Usuario> buscar(@PathVariable Long id) {
        return new ResponseEntity<>(service.buscar(id), HttpStatus.OK);
    }

    // Desactivar
    @PutMapping("/desactivar/{id}")
    public ResponseEntity<?> desactivar(@PathVariable Long id) {
        service.desactivar(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Activar
    @PutMapping("/activar/{id}")
    public ResponseEntity<?> activar(@PathVariable Long id) {
        service.activar(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // ✔ Eliminar usuario
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}