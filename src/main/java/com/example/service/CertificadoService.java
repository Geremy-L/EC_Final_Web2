package com.example.service;

import com.example.dto.CertificadoResponse;
import com.example.dto.CursoResponse;
import com.example.dto.UsuarioResponse;
import com.example.exception.BadRequestException;
import com.example.exception.ResourceNotFoundException;
import com.example.model.*;
import com.example.repository.*;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CertificadoService {

    private final CertificadoRepository certificadoRepository;
    private final InscripcionRepository inscripcionRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;

    // Llamado automáticamente desde InscripcionService cuando nota >= 11
    public void emitirAutomatico(Long alumnoId, Long cursoId) {
        if (certificadoRepository.existsByCursoIdAndAlumnoId(cursoId, alumnoId)) {
            throw new BadRequestException("Ya existe un certificado para este alumno en este curso");
        }
        Usuario alumno = usuarioRepository.findById(alumnoId)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado"));
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));

        String codigo = "CERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Certificado cert = Certificado.builder()
                .codigo(codigo).alumno(alumno).curso(curso).build();
        certificadoRepository.save(cert);
    }

    // Emitir manualmente (Admin/Docente)
    public CertificadoResponse emitir(Long alumnoId, Long cursoId) {
        Usuario alumno = usuarioRepository.findById(alumnoId)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado"));
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));

        Inscripcion inscripcion = inscripcionRepository
                .findByAlumnoIdAndCursoId(alumnoId, cursoId)
                .orElseThrow(() -> new ResourceNotFoundException("El alumno no está inscrito en este curso"));

        if (inscripcion.getEstado() != Inscripcion.EstadoInscripcion.APROBADO) {
            throw new BadRequestException("El alumno debe tener estado APROBADO para obtener el certificado");
        }
        if (certificadoRepository.existsByCursoIdAndAlumnoId(cursoId, alumnoId)) {
            throw new BadRequestException("Ya existe un certificado para este alumno en este curso");
        }

        String codigo = "CERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Certificado cert = Certificado.builder()
                .codigo(codigo).alumno(alumno).curso(curso).build();
        return toResponse(certificadoRepository.save(cert));
    }

    public List<CertificadoResponse> listar() {
        return certificadoRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<CertificadoResponse> listarPorAlumno(Long alumnoId) {
        if (!usuarioRepository.existsById(alumnoId))
            throw new ResourceNotFoundException("Alumno no encontrado");
        return certificadoRepository.findByAlumnoId(alumnoId).stream().map(this::toResponse).toList();
    }

    public CertificadoResponse buscar(Long id) {
        return toResponse(certificadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificado no encontrado")));
    }

    // Buscar por código único (ej: CERT-A1B2C3D4)
    public CertificadoResponse buscarPorCodigo(String codigo) {
        return toResponse(certificadoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Certificado no encontrado con código: " + codigo)));
    }

    public void eliminar(Long id) {
        if (!certificadoRepository.existsById(id))
            throw new ResourceNotFoundException("Certificado no encontrado");
        certificadoRepository.deleteById(id);
    }

    // Generar PDF del certificado por código único
    public byte[] generarPdf(String codigo) {
        Certificado c = certificadoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Certificado no encontrado con código: " + codigo));

        // Obtener nota de la inscripción
        Double nota = inscripcionRepository
                .findByAlumnoIdAndCursoId(c.getAlumno().getId(), c.getCurso().getId())
                .map(Inscripcion::getNota)
                .orElse(null);

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4.rotate(), 40, 40, 40, 40);
            PdfWriter writer = PdfWriter.getInstance(doc, out);
            doc.open();

            PdfContentByte canvas = writer.getDirectContentUnder();

            // Fondo degradado azul oscuro
            canvas.setColorFill(new Color(10, 36, 99));
            canvas.rectangle(0, 0, doc.getPageSize().getWidth(), doc.getPageSize().getHeight());
            canvas.fill();

            // Borde dorado decorativo exterior
            canvas.setColorStroke(new Color(212, 175, 55));
            canvas.setLineWidth(6f);
            canvas.rectangle(20, 20, doc.getPageSize().getWidth() - 40, doc.getPageSize().getHeight() - 40);
            canvas.stroke();

            // Borde dorado interior
            canvas.setLineWidth(2f);
            canvas.rectangle(30, 30, doc.getPageSize().getWidth() - 60, doc.getPageSize().getHeight() - 60);
            canvas.stroke();

            // ── Fuentes ──
            Font fTitulo    = new Font(Font.HELVETICA, 38, Font.BOLD,   new Color(212, 175, 55));
            Font fSubtitulo = new Font(Font.HELVETICA, 14, Font.ITALIC, new Color(200, 200, 200));
            Font fLabel     = new Font(Font.HELVETICA, 12, Font.NORMAL, new Color(180, 180, 180));
            Font fNombre    = new Font(Font.HELVETICA, 30, Font.BOLD,   Color.WHITE);
            Font fCurso     = new Font(Font.HELVETICA, 18, Font.BOLDITALIC, new Color(212, 175, 55));
            Font fCodigo    = new Font(Font.HELVETICA,  9, Font.NORMAL, new Color(150, 150, 150));
            Font fNota      = new Font(Font.HELVETICA, 13, Font.BOLD,   new Color(212, 175, 55));

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy",
                    new java.util.Locale("es", "PE"));

            // ── Contenido ──
            Paragraph spacer = new Paragraph(" ");
            spacer.setSpacingAfter(10);

            // Institución
            Paragraph inst = new Paragraph("EstudiaPE", fTitulo);
            inst.setAlignment(Element.ALIGN_CENTER);
            inst.setSpacingBefore(20);
            doc.add(inst);

            Paragraph instSub = new Paragraph("Sistema de Gestión para Cursos Online", fSubtitulo);
            instSub.setAlignment(Element.ALIGN_CENTER);
            doc.add(instSub);

            // Línea separadora dorada
            doc.add(spacer);
            LineSeparator line = new LineSeparator(1.5f, 60f, new Color(212, 175, 55), Element.ALIGN_CENTER, -5);
            doc.add(new Chunk(line));
            doc.add(spacer);

            // Texto principal
            Paragraph otorga = new Paragraph("Certifica que:", fLabel);
            otorga.setAlignment(Element.ALIGN_CENTER);
            otorga.setSpacingBefore(8);
            doc.add(otorga);

            Paragraph nombre = new Paragraph(c.getAlumno().getNombre().toUpperCase(), fNombre);
            nombre.setAlignment(Element.ALIGN_CENTER);
            nombre.setSpacingBefore(6);
            nombre.setSpacingAfter(6);
            doc.add(nombre);

            Paragraph completoLabel = new Paragraph("ha completado satisfactoriamente el curso:", fLabel);
            completoLabel.setAlignment(Element.ALIGN_CENTER);
            doc.add(completoLabel);

            Paragraph curso = new Paragraph(c.getCurso().getTitulo(), fCurso);
            curso.setAlignment(Element.ALIGN_CENTER);
            curso.setSpacingBefore(6);
            curso.setSpacingAfter(10);
            doc.add(curso);

            // Nota si existe
            if (nota != null) {
                Paragraph notaP = new Paragraph("Nota obtenida: " + nota + " / 20", fNota);
                notaP.setAlignment(Element.ALIGN_CENTER);
                notaP.setSpacingAfter(8);
                doc.add(notaP);
            }

            // Fecha
            String fechaStr = c.getEmitidoEn() != null
                    ? c.getEmitidoEn().format(fmt)
                    : java.time.LocalDateTime.now().format(fmt);
            Paragraph fecha = new Paragraph("Emitido el " + fechaStr, fLabel);
            fecha.setAlignment(Element.ALIGN_CENTER);
            fecha.setSpacingAfter(12);
            doc.add(fecha);

            // Línea separadora
            doc.add(new Chunk(new LineSeparator(1f, 50f, new Color(212, 175, 55), Element.ALIGN_CENTER, -5)));
            doc.add(spacer);

            // Código único
            Paragraph codigoP = new Paragraph("Código de verificación: " + c.getCodigo(), fCodigo);
            codigoP.setAlignment(Element.ALIGN_CENTER);
            codigoP.setSpacingBefore(4);
            doc.add(codigoP);

            Paragraph verificar = new Paragraph(
                    "Verifique este certificado en: GET /api/certificados/codigo/" + c.getCodigo(), fCodigo);
            verificar.setAlignment(Element.ALIGN_CENTER);
            doc.add(verificar);

            doc.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF: " + e.getMessage());
        }
    }

    private CertificadoResponse toResponse(Certificado c) {
        return CertificadoResponse.builder()
                .id(c.getId())
                .codigo(c.getCodigo())
                .emitidoEn(c.getEmitidoEn())
                .alumno(toUsuarioResponse(c.getAlumno()))
                .curso(toCursoResponse(c.getCurso()))
                .build();
    }

    private UsuarioResponse toUsuarioResponse(Usuario u) {
        return UsuarioResponse.builder()
                .id(u.getId()).nombre(u.getNombre()).email(u.getEmail())
                .activo(u.getActivo())
                .roles(u.getRoles().stream().map(r -> r.getNombre().name()).toList())
                .build();
    }

    private CursoResponse toCursoResponse(Curso curso) {
        return CursoResponse.builder()
                .id(curso.getId()).titulo(curso.getTitulo())
                .descripcion(curso.getDescripcion()).activo(curso.getActivo())
                .creadoEn(curso.getCreadoEn())
                .docente(toUsuarioResponse(curso.getDocente()))
                .build();
    }
}
