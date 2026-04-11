package com.example.service;

import com.example.dto.CursoResponse;
import com.example.dto.InscripcionRequest;
import com.example.dto.InscripcionResponse;
import com.example.dto.UsuarioResponse;
import com.example.model.*;
import com.example.repository.*;
import com.example.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InscripcionService {

    private final InscripcionRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;

    public List<InscripcionResponse> listar() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public InscripcionResponse buscar(Long id) {
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción no encontrada")));
    }

    public InscripcionResponse crear(InscripcionRequest req) {

        Usuario alumno = usuarioRepository.findById(req.getAlumnoId())
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado"));

        Curso curso = cursoRepository.findById(req.getCursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));

        if (repository.existsByAlumnoIdAndCursoId(req.getAlumnoId(), req.getCursoId())) {
            throw new RuntimeException("El alumno ya está inscrito en este curso");
        }

        Inscripcion inscripcion = Inscripcion.builder()
                .alumno(alumno)
                .curso(curso)
                .build();

        return toResponse(repository.save(inscripcion));
    }

    public InscripcionResponse actualizarEstado(Long id, String estadoStr) {

        Inscripcion inscripcion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción no encontrada"));

        Inscripcion.EstadoInscripcion estado =
                Inscripcion.EstadoInscripcion.valueOf(estadoStr);

        inscripcion.setEstado(estado);

        return toResponse(repository.save(inscripcion));
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Inscripción no encontrada");
        }
        repository.deleteById(id);
    }

    private InscripcionResponse toResponse(Inscripcion i) {
        return InscripcionResponse.builder()
                .id(i.getId())
                .alumno(toUsuarioResponse(i.getAlumno()))
                .curso(toCursoResponse(i.getCurso()))
                .estado(i.getEstado().name())
                .fechaInscripcion(i.getFechaInscripcion())
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
}