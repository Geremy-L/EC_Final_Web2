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
    private final CertificadoService certificadoService;

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
            throw new BadRequestException("El alumno ya está inscrito en este curso");
        }

        Inscripcion inscripcion = Inscripcion.builder()
                .alumno(alumno)
                .curso(curso)
                .build();

        return toResponse(repository.save(inscripcion));
    }

    // Docente coloca la nota → estado automático según nota
    public InscripcionResponse calificar(Long id, Double nota) {

        if (nota < 0 || nota > 20) {
            throw new BadRequestException("La nota debe estar entre 0 y 20");
        }

        Inscripcion inscripcion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción no encontrada"));

        if (inscripcion.getEstado() == Inscripcion.EstadoInscripcion.CANCELADO) {
            throw new BadRequestException("No se puede calificar una inscripción cancelada");
        }

        inscripcion.setNota(nota);

        // Estado automático según nota
        if (nota >= 11) {
            inscripcion.setEstado(Inscripcion.EstadoInscripcion.APROBADO);
            // Generar certificado automáticamente
            try {
                certificadoService.emitirAutomatico(
                        inscripcion.getAlumno().getId(),
                        inscripcion.getCurso().getId()
                );
            } catch (BadRequestException e) {
                // Ya existe certificado, no pasa nada
            }
        } else {
            inscripcion.setEstado(Inscripcion.EstadoInscripcion.DESAPROBADO);
        }

        return toResponse(repository.save(inscripcion));
    }

    public InscripcionResponse cancelar(Long id) {
        Inscripcion inscripcion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción no encontrada"));
        inscripcion.setEstado(Inscripcion.EstadoInscripcion.CANCELADO);
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
                .nota(i.getNota())
                .fechaInscripcion(i.getFechaInscripcion())
                .build();
    }

    private UsuarioResponse toUsuarioResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .activo(usuario.getActivo())
                .roles(usuario.getRoles().stream()
                        .map(r -> r.getNombre().name())
                        .toList())
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
