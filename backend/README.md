# SanLuApp - Backend Configuration

## Desarrollo Local

### Ejecutar con limpieza automática de BD

Para ejecutar la aplicación en desarrollo con limpieza automática del esquema anterior:

**En Windows (PowerShell):**
```powershell
cd backend
.\run-local.ps1
```

**En Linux/Mac (Bash):**
```bash
cd backend
./run-local.sh
```

### Configuración de Base de Datos

La aplicación está configurada para:

- **Desarrollo Local (`application-local.properties`):**
  - BD: PostgreSQL en `localhost:5432`
  - Usuario: `sanluapp`
  - Contraseña: `sanluapp`
  - DDL Auto: `create-drop` (crea y destruye tablas en cada inicio)
  - Flyway: Limpia el esquema antes de ejecutar migraciones

- **Producción (`pro.env`):**
  - DDL Auto: `validate` (solo valida)
  - Flyway: `clean-disabled=true` (no limpia)

### Migraciones de Base de Datos

Las migraciones se encuentran en:
```
backend/src/main/resources/db/migration/
```

**Archivos de migración actuales:**
- `V1__init_user_tables.sql` - Crea tablas de usuarios, roles y relaciones


### Tecnologías

- **Spring Boot:** 3.5.7
- **Java:** 25
- **PostgreSQL:** 16
- **Flyway:** 10.15.0
- **Hibernate:** 6.6.33
- **Lombok:** Para reducir boilerplate

### Características

- ✅ Autolimpieza de BD en cada arranque (desarrollo)
- ✅ Migraciones automáticas con Flyway
- ✅ Perfiles de Spring para desarrollo/producción
- ✅ JPA/Hibernate + PostgreSQL
- ✅ Lombok para entidades simplificadas

### Próximos Pasos

1. Crear entidades JPA (Usuario, Rol, etc.)
2. Implementar repositorios Spring Data JPA
3. Crear controladores REST
4. Conectar con frontend Angular v20
