#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Genera el directorio HTML de JavaDoc de un proyecto Java que usa Maven.

Uso:
    python generar_javadoc.py
    python generar_javadoc.py "C:/ruta/a/mi/proyecto"
    python generar_javadoc.py "C:/ruta/a/mi/proyecto" --salida "docs/javadoc"

Requisitos:
    - Python 3
    - JDK instalado (comando javadoc disponible)
    - Maven instalado (comando mvn disponible)
    - El proyecto debe tener un archivo pom.xml

Este script no modifica los archivos fuente Java.
"""

import argparse
import shutil
import subprocess
import sys
from pathlib import Path

EXCLUDED_DIRS = {
    ".git", ".idea", ".vscode", "target", "build", "out",
    "node_modules", ".gradle", "__pycache__"
}


def encontrar_archivos_java(raiz: Path):
    """Busca archivos .java sin recorrer carpetas generadas o de control de versiones."""
    archivos = []
    for ruta in raiz.rglob("*.java"):
        if any(parte in EXCLUDED_DIRS for parte in ruta.parts):
            continue
        archivos.append(ruta)
    return sorted(archivos)


def main():
    parser = argparse.ArgumentParser(
        description="Genera la documentación JavaDoc HTML de un proyecto Maven."
    )
    parser.add_argument(
        "proyecto",
        nargs="?",
        default=".",
        help="Ruta raíz del proyecto (por defecto, la carpeta actual)."
    )
    parser.add_argument(
        "--salida",
        default="documentacion-javadoc",
        help="Directorio de salida, relativo a la raíz del proyecto "
             "(por defecto: documentacion-javadoc)."
    )
    args = parser.parse_args()

    raiz = Path(args.proyecto).expanduser().resolve()
    if not raiz.is_dir():
        print(f"ERROR: la carpeta del proyecto no existe: {raiz}", file=sys.stderr)
        return 1

    pom = raiz / "pom.xml"
    if not pom.is_file():
        print(
            "ERROR: no se encontró pom.xml en la carpeta indicada.\n"
            "Ejecuta el script desde la raíz del proyecto Maven o indica su ruta.",
            file=sys.stderr
        )
        return 1

    if shutil.which("mvn") is None:
        print(
            "ERROR: no se encontró el comando 'mvn'. Instala Maven y verifica "
            "que esté agregado al PATH.",
            file=sys.stderr
        )
        return 1

    if shutil.which("javadoc") is None:
        print(
            "ERROR: no se encontró el comando 'javadoc'. Instala un JDK "
            "(no solamente un JRE) y verifica que esté agregado al PATH.",
            file=sys.stderr
        )
        return 1

    fuentes = encontrar_archivos_java(raiz)
    if not fuentes:
        print("ERROR: no se encontraron archivos .java en el proyecto.", file=sys.stderr)
        return 1

    salida = Path(args.salida)
    if not salida.is_absolute():
        salida = (raiz / salida).resolve()

    # Evita que la carpeta de salida quede dentro de una carpeta fuente
    # y termine siendo interpretada como parte del proyecto.
    salida.mkdir(parents=True, exist_ok=True)

    print(f"Proyecto: {raiz}")
    print(f"Archivos Java encontrados: {len(fuentes)}")
    print(f"Directorio de documentación: {salida}")
    print("\nGenerando JavaDoc con Maven. Esto puede tardar unos minutos...\n")

    mvn = shutil.which("mvn")
    if mvn is None:
        print(
            "ERROR: no se encontró el comando 'mvn'. Instala Maven y verifica "
            "que esté agregado al PATH.",
            file=sys.stderr
        )
        return 1
    
    comando = [
        mvn,
        "-f", str(pom),
        "compile",
        "javadoc:javadoc"
    ]

    try:
        resultado = subprocess.run(comando, cwd=raiz, check=False)
    except OSError as error:
        print(f"ERROR al ejecutar Maven: {error}", file=sys.stderr)
        return 1

    if resultado.returncode != 0:
        print(
            "\nERROR: Maven no pudo generar el JavaDoc. Revisa los mensajes "
            "anteriores; pueden indicar errores de compilación, dependencias "
            "faltantes o comentarios/documentación con problemas.",
            file=sys.stderr
        )
        return resultado.returncode

    # El plugin puede escribir en el directorio configurado o en la ubicación
    # predeterminada de Maven según la configuración del proyecto.
    origen = raiz / "target" / "reports" / "apidocs"
    if origen.is_dir():
        shutil.copytree(origen, salida, dirs_exist_ok=True)
        
    posibles_indices = [
        salida / "index.html",
        raiz / "target" / "reports" / "apidocs" / "index.html",
        raiz / "target" / "site" / "apidocs" / "index.html",
    ]
    indice = next((p for p in posibles_indices if p.is_file()), None)

    if indice:
        print("\n¡JavaDoc generado correctamente!")
        print(f"Abre este archivo en tu navegador: {indice}")
        print(
            "\nEntrega la carpeta completa de documentación, no solo index.html, "
            "porque contiene las páginas de clases, paquetes, métodos y recursos."
        )
    else:
        print(
            "\nMaven terminó sin reportar errores, pero no se encontró index.html "
            "en las rutas habituales. Busca index.html dentro de la carpeta "
            "'target' o revisa la configuración de maven-javadoc-plugin."
        )

    return 0


if __name__ == "__main__":
    raise SystemExit(main())



