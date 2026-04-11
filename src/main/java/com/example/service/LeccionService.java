package com.example.service;

import com.example.dto.CursoResponse;
import com.example.dto.LeccionResponse;
import com.example.dto.UsuarioResponse;
import com.example.model.*;
import com.example.repository.*;
import com.example.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeccionService {

    private final LeccionRepository repository;
    private final CursoRepository cursoRepository;

    public List<LeccionResponse> listar() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public LeccionResponse buscar(Long id) {
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lección no encontrada")));
    }

    public List<LeccionResponse> listarPorCurso(Long cursoId) {
        return repository.findByCursoIdOrderByOrdenAsc(cursoId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public LeccionResponse crear(Leccion leccion) {

        if (leccion.getCurso() == null || leccion.getCurso().getId() == null) {
            throw new RuntimeException("Debe enviar el id del curso");
        }

        Curso curso = cursoRepository.findById(leccion.getCurso().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));

        leccion.setCurso(curso);

        return toResponse(repository.save(leccion));
    }

    public LeccionResponse actualizar(Long id, Leccion datos) {

        Leccion leccion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lección no encontrada"));

        if (datos.getTitulo() != null) {
            leccion.setTitulo(datos.getTitulo());
        }

        if (datos.getContenido() != null) {
            leccion.setContenido(datos.getContenido());
        }

        if (datos.getOrden() != null) {
            leccion.setOrden(datos.getOrden());
        }

        if (datos.getCurso() != null && datos.getCurso().getId() != null) {
            Curso curso = cursoRepository.findById(datos.getCurso().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));
            leccion.setCurso(curso);
        }

        return toResponse(repository.save(leccion));
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Lección no encontrada");
        }
        repository.deleteById(id);
    }

    private LeccionResponse toResponse(Leccion leccion) {
        return LeccionResponse.builder()
                .titulo(leccion.getTitulo())
                .contenido(leccion.getContenido())
                .orden(leccion.getOrden())
                .curso(toCursoResponse(leccion.getCurso()))
                .build();
    }

    private CursoResponse toCursoResponse(Curso curso) {
        return CursoResponse.builder()
                .id(curso.getId())
                .titulo(curso.getTitulo())
                .descripcion(curso.getDescripcion())
                .activo(curso.getActivo())
                .creadoEn(curso.getCreadoEn())
                .docente(toUsuarioResponse(curso.getDocente()))
                .build();
    }

    private UsuarioResponse toUsuarioResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .activo(usuario.getActivo())
                .roles(
                        usuario.getRoles().stream()
                                .map(r -> r.getNombre().name())
                                .toList()
                )
                .build();
    }
}