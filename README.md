# EstudiaPE - Sistema de Gestión para Cursos Online

## 🛠️ Tecnologías
- Java 17 + Spring Boot 3
- Spring Security + JWT
- JPA / Hibernate + MySQL
- Lombok, Maven

## ⚙️ Configuración

Edita `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/estudia_pe
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD
jwt.secret=EstudiaPE_SecretKey_SuperSegura_2024_XYZ123456789
jwt.expiration=86400000
server.port=8080
```

## 🚀 Cómo ejecutar

1. Crear base de datos en MySQL:
```sql
CREATE DATABASE estudia_pe;
```

2. Ejecutar el proyecto:
```bash
mvn spring-boot:run
```

3. El sistema crea automáticamente el usuario Admin:
   - Email: `admin@admin.com`
   - Password: `123456`

## 👤 Usuarios de prueba

| Rol | Email | Password |
|-----|-------|----------|
| ADMIN | admin@admin.com | 123456 |
| DOCENTE | docente@estudia.pe | 123456 |
| ALUMNO | alumno@estudia.pe | 123456 |

> Crear docente y alumno con el endpoint POST /api/usuarios/crear (autenticado como Admin)

## 📋 Endpoints principales

| Método | Endpoint | Rol |
|--------|----------|-----|
| POST | /api/auth/login | Público |
| POST | /api/auth/register | Público |
| GET | /api/cursos/listar | Autenticado |
| POST | /api/cursos/crear | DOCENTE / ADMIN |
| POST | /api/inscripciones/crear | ALUMNO |
| POST | /api/certificados/emitir | DOCENTE / ADMIN |
| GET | /api/usuarios/listar | ADMIN |

## 📦 Importar colección Postman
Importar el archivo `EstudiaPE_Postman_Collection.json` en Postman.
