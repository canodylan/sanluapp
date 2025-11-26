# Base de Datos
La aplicación utiliza PostgreSQL como sistema de gestión de bases de datos. A continuación se detallan los pasos para configurar y ejecutar la base de datos utilizando Docker.

## Configuración de Docker Compose
El archivo `docker-compose.yml` incluye un servicio para la base de datos PostgreSQL. Asegúrate de que el archivo contiene la siguiente configuración para el servicio de la base de datos:

```yaml
version: '3.9'
services:	