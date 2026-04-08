package com.example.service;

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

    public List<Inscripcion> listar() {
        return repository.findAll();
    }

    public Inscripcion inscribir(Long alumnoId, Long cursoId) {

        if (repository.findByAlumnoIdAndCursoId(alumnoId, cursoId).isPresent()) {
            throw new BusinessException("Ya está inscrito en este curso");
        }

        Usuario alumno = usuarioRepository.findById(alumnoId)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado"));

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));

        Inscripcion inscripcion = Inscripcion.builder()
                .alumno(alumno)
                .curso(curso)
                .build();

        return repository.save(inscripcion);
    }
}