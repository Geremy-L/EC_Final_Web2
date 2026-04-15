package com.example.repository;

import com.example.model.Certificado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificadoRepository extends JpaRepository<Certificado, Long> {

    List<Certificado> findByAlumnoId(Long alumnoId);

    Optional<Certificado> findByCursoIdAndAlumnoId(Long cursoId, Long alumnoId);

    Optional<Certificado> findByCodigo(String codigo);

    boolean existsByCursoIdAndAlumnoId(Long cursoId, Long alumnoId);
}
