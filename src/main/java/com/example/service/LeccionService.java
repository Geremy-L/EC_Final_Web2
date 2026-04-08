package com.example.service;

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

    public List<Leccion> listarPorCurso(Long cursoId) {
        return repository.findByCursoIdOrderByOrdenAsc(cursoId);
    }

    public Leccion agregar(Long cursoId, Leccion leccion) {

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));

        leccion.setCurso(curso);

        return repository.save(leccion);
    }
}