#!/usr/bin/env bash
set -euo pipefail

if ! command -v java >/dev/null 2>&1; then
    echo "Error: java not found in PATH. Install JDK first." >&2
    exit 1
fi

ROOT="$(cd "$(dirname "$0")" && pwd)"
java -cp "$ROOT/out" com.example.dungeon.Main