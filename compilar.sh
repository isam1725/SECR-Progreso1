#!/usr/bin/env bash
# Compila SECR y genera dist/SECR.jar (requiere JDK 17 o superior)
set -e
cd "$(dirname "$0")"
rm -rf out && mkdir -p out dist
javac --release 17 -encoding UTF-8 -d out $(find src/main/java -name "*.java")
jar --create --file dist/SECR.jar --main-class interfaz.Interfaz -C out .
echo "Listo: dist/SECR.jar  ->  java -jar dist/SECR.jar"
