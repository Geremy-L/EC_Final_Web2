package com.example.service;

import com.example.model.Categoria;
import com.example.repository.CategoriaRepository;
import com.example.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository repository;

    public List<Categoria> listar() {
        return repository.findAll();
    }

    public Categoria buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada"));
    }

    public Categoria agregar(Categoria categoria) {
        return repository.save(categoria);
    }

    public Categoria editar(Long id, Categoria nueva) {
        Categoria c = buscar(id);
        c.setNombre(nueva.getNombre());
        c.setDescripcion(nueva.getDescripcion());
        return repository.save(c);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}