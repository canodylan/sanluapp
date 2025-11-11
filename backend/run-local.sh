#!/bin/bash
# Script para ejecutar la aplicación en desarrollo con limpieza de BD automática
# Uso: ./run-local.sh

echo "==============================================="
echo "SanLu App - Ejecutando en modo LOCAL"
echo "==============================================="
echo ""


echo ""
echo "1. Compilando aplicación..."
./mvnw clean compile
if [ $? -ne 0 ]; then
    echo "Error al compilar"
    exit 1
fi

echo ""
echo "2. Ejecutando aplicación..."
echo "La aplicación estará disponible en: http://localhost:8080"
echo ""

./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
