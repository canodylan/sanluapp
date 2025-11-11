# Script para ejecutar la aplicacion en desarrollo con limpieza de BD automática
# Uso: .\run-local.ps1

Write-Host "===============================================" -ForegroundColor Cyan
Write-Host "SanLu App - Ejecutando en modo LOCAL" -ForegroundColor Cyan
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host ""

Write-Host ""
Write-Host "1. Compilando aplicacion..." -ForegroundColor Yellow
& ".\mvnw.cmd" clean compile
if ($LASTEXITCODE -ne 0) {
    Write-Host "Error al compilar" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "2. Ejecutando aplicacion..." -ForegroundColor Green
Write-Host "La aplicacion estará disponible en: http://localhost:8080" -ForegroundColor Green
Write-Host ""

& ".\mvnw.cmd" spring-boot:run "-Dspring-boot.run.arguments=--spring.profiles.active=local"
