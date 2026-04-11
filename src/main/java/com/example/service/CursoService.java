package com.example.service;

import com.example.dto.CursoResponse;
import com.example.dto.UsuarioResponse;
import com.example.model.*;
import com.example.repository.*;
import com.example.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;

    public List<CursoResponse> listar() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CursoResponse buscar(Long id) {
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado")));
    }

    public CursoResponse crear(Curso curso) {

        if (curso.getActivo() == null) {
            curso.setActivo(true);
        }

        Usuario docente = usuarioRepository.findById(curso.getDocente().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado"));

        Categoria categoria = categoriaRepository.findById(curso.getCategoria().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        curso.setDocente(docente);
        curso.setCategoria(categoria);

        return toResponse(repository.save(curso));
    }

    public CursoResponse actualizar(Long id, Curso datos) {

        Curso curso = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));

        if (datos.getTitulo() != null) {
            curso.setTitulo(datos.getTitulo());
        }

        if (datos.getDescripcion() != null) {
            curso.setDescripcion(datos.getDescripcion());
        }

        if (datos.getActivo() != null) {
            curso.setActivo(datos.getActivo());
        }

        if (datos.getDocente() != null) {
            Usuario docente = usuarioRepository.findById(datos.getDocente().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado"));
            curso.setDocente(docente);
        }

        if (datos.getCategoria() != null) {
            Categoria categoria = categoriaRepository.findById(datos.getCategoria().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
            curso.setCategoria(categoria);
        }

        return toResponse(repository.save(curso));
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Curso no encontrado");
        }
        repository.deleteById(id);
    }

    public CursoResponse desactivar(Long id) {
        Curso curso = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));

        curso.setActivo(false);
        return toResponse(repository.save(curso));
    }

    public CursoResponse activar(Long id) {
        Curso curso = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));

        curso.setActivo(true);
        return toResponse(repository.save(curso));
    }

    private CursoResponse toResponse(Curso curso) {
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