set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
OUT="$ROOT/out"

if ! command -v javac >/dev/null 2>&1; then
    echo "Error: javac not found in PATH. Install JDK first." >&2
    exit 1
fi

rm -rf "$OUT"
mkdir -p "$OUT"

# Компиляция всех .java файлов
find "$ROOT/src" -name "*.java" -exec javac -encoding UTF-8 -d "$OUT" {} +

echo "Build OK. Classes in $OUT"