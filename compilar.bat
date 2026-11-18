@echo off
REM Compila SECR y genera dist\SECR.jar (requiere JDK 17 o superior)
cd /d "%~dp0"
if exist out rmdir /s /q out
mkdir out 2>nul
mkdir dist 2>nul
dir /s /b src\main\java\*.java > fuentes.txt
javac --release 17 -encoding UTF-8 -d out @fuentes.txt
if errorlevel 1 goto error
del fuentes.txt
jar --create --file dist\SECR.jar --main-class interfaz.Interfaz -C out .
echo Listo: dist\SECR.jar
goto fin
:error
echo Error de compilacion.
:fin
pause
