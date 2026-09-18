#!/usr/bin/env bash
# ==============================================================================
# CampusFlow Test Execution Script
# Runs automated unit and validation tests.
# ==============================================================================
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

if command -v mvn >/dev/null 2>&1; then
    echo "[INFO] Running full test suite with Maven..."
    mvn test
else
    echo "[INFO] Running standalone native test suite..."
    mkdir -p bin
    find src/main/java src/test/java/com/campusflow/StandaloneTestRunner.java -name "*.java" > test_sources.txt
    javac -d bin -sourcepath src/main/java @test_sources.txt
    rm -f test_sources.txt
    java -cp bin com.campusflow.StandaloneTestRunner
fi
