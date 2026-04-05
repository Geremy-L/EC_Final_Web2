package com.example.repository;

import com.example.model.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    Optional<Inscripcion> findByAlumnoIdAndCursoId(Long alumnoId, Long cursoId);

    List<Inscripcion> findByAlumnoId(Long alumnoId);

    List<Inscripcion> findByCursoId(Long cursoId);
}