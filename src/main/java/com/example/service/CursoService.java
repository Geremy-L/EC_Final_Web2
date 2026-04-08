package com.example.service;

import com.example.model.Curso;
import com.example.repository.CursoRepository;
import com.example.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository repository;

    public List<Curso> listar() {
        return repository.findAll();
    }

    public Curso buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));
    }

    public Curso agregar(Curso curso) {
        return repository.save(curso);
    }

    public Curso editar(Long id, Curso nuevo) {
        Curso curso = buscar(id);

        curso.setTitulo(nuevo.getTitulo());
        curso.setDescripcion(nuevo.getDescripcion());
        curso.setActivo(nuevo.isActivo());

        return repository.save(curso);
    }

    public void eliminar(Long id) {
        Curso curso = buscar(id);
        repository.delete(curso);
    }
}