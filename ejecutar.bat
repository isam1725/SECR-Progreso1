@echo off
REM Ejecuta la aplicación gráfica de SECR (requiere Java 17 o superior)
cd /d "%~dp0"
java -Dfile.encoding=UTF-8 -jar dist\SECR.jar
if errorlevel 1 pause
