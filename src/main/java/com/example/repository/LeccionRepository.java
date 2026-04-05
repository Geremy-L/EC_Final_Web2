package com.example.repository;

import com.example.model.Leccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeccionRepository extends JpaRepository<Leccion, Long> {

    List<Leccion> findByCursoId(Long cursoId);

    List<Leccion> findByCursoIdOrderByOrdenAsc(Long cursoId);

}