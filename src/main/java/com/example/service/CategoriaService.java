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
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
    }

    public Categoria agregar(Categoria categoria) {

        repository.findByNombre(categoria.getNombre())
                .ifPresent(c -> {
                    throw new IllegalArgumentException("La categoría ya existe");
                });

        return repository.save(categoria);
    }

    public Categoria editar(Long id, Categoria nueva) {

        Categoria c = buscar(id);

        if (!c.getNombre().equals(nueva.getNombre())) {
            repository.findByNombre(nueva.getNombre())
                    .ifPresent(cat -> {
                        throw new IllegalArgumentException("La categoría ya existe");
                    });
        }

        c.setNombre(nueva.getNombre());
        c.setDescripcion(nueva.getDescripcion());

        return repository.save(c);
    }

    public void eliminar(Long id) {
        Categoria c = buscar(id); // valida existencia
        repository.delete(c);
    }
}