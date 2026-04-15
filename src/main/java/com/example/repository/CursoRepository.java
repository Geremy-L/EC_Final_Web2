package com.example.repository;

import com.example.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    List<Curso> findByActivo(boolean activo);

    List<Curso> findByCategoriaId(Long categoriaId);

    List<Curso> findByDocenteId(Long docenteId);

    List<Curso> findByTituloContainingIgnoreCase(String titulo);
}
