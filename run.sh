#!/usr/bin/env bash
# ==============================================================================
# CampusFlow Run Script
# Usage:
#   ./run.sh          -> Launches interactive CLI menu
#   ./run.sh --demo   -> Launches non-interactive automated test & demo run
# ==============================================================================
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

if [ ! -d "bin" ] && [ ! -f "target/campusflow-system-1.0.0.jar" ]; then
    echo "[INFO] Compiled classes not found. Running ./build.sh first..."
    bash ./build.sh
fi

if [ -f "target/campusflow-system-1.0.0.jar" ]; then
    java -jar target/campusflow-system-1.0.0.jar "$@"
else
    java -cp bin com.campusflow.CampusFlowApp "$@"
fi
