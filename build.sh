#!/usr/bin/env bash
# ==============================================================================
# CampusFlow Build Script (Zero External Dependencies Required)
# Compiles using javac into bin/ directory or invokes mvn package if available.
# ==============================================================================
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "=========================================================="
echo "          Building CampusFlow Project Engine             "
echo "=========================================================="

if command -v mvn >/dev/null 2>&1; then
    echo "[INFO] Maven detected. Compiling and packaging with Maven..."
    mvn clean package -DskipTests
    echo "[SUCCESS] Maven build complete: target/campusflow-system-1.0.0.jar"
else
    echo "[INFO] Compiling directly with javac..."
    mkdir -p bin
    find src/main/java -name "*.java" > sources.txt
    javac -d bin -sourcepath src/main/java @sources.txt
    rm -f sources.txt
    echo "[SUCCESS] Java classes compiled into bin/ directory."
fi
