package com.example.controller;

import com.example.dto.CertificadoResponse;
import com.example.service.CertificadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificados")
@RequiredArgsConstructor
public class CertificadoController {

    private final CertificadoService service;

    // Listar todos (Admin)
    @GetMapping("/listar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CertificadoResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    // Buscar por ID
    @GetMapping("/buscar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALUMNO', 'DOCENTE')")
    public ResponseEntity<CertificadoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    // Buscar por código único → cualquiera puede verificar
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<CertificadoResponse> buscarPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(service.buscarPorCodigo(codigo));
    }

    // Ver certificados de un alumno
    @GetMapping("/alumno/{alumnoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALUMNO', 'DOCENTE')")
    public ResponseEntity<List<CertificadoResponse>> listarPorAlumno(@PathVariable Long alumnoId) {
        return ResponseEntity.ok(service.listarPorAlumno(alumnoId));
    }

    // Emitir manualmente si el alumno ya está APROBADO (Admin/Docente)
    @PostMapping("/emitir")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE')")
    public ResponseEntity<CertificadoResponse> emitir(
            @RequestParam Long alumnoId,
            @RequestParam Long cursoId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.emitir(alumnoId, cursoId));
    }

    // Descargar PDF por código único
    @GetMapping("/descargar/{codigo}")
    public ResponseEntity<byte[]> descargar(@PathVariable String codigo) {
        byte[] pdf = service.generarPdf(codigo);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"certificado-" + codigo + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // Eliminar (Admin)
    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
