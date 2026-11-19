#!/usr/bin/env bash
# Local F0 helper when Gradle daemon is memory-constrained.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
OUT="${TMPDIR:-/tmp}/mimir-kotlinc-out"
mkdir -p "$OUT"
KC="${KOTLINC:-kotlinc}"
COROUTINES="${COROUTINES_JAR:-$OUT/kotlinx-coroutines-core-jvm-1.9.0.jar}"
if [[ ! -f "$COROUTINES" ]]; then
  curl -sL "https://repo1.maven.org/maven2/org/jetbrains/kotlinx/kotlinx-coroutines-core-jvm/1.9.0/kotlinx-coroutines-core-jvm-1.9.0.jar" -o "$COROUTINES"
fi
$KC -jvm-target 17 -cp "$COROUTINES" -d "$OUT/kernel.jar" $(find "$ROOT/kernel/src/main" -name '*.kt')
CP="$OUT/kernel.jar:$COROUTINES"
for c in "$ROOT"/contracts/*/src/main; do
  [[ -d $c ]] || continue
  name=$(basename $(dirname $(dirname $c)))
  files=$(find "$c" -name '*.kt')
  [[ -n $files ]] || continue
  $KC -jvm-target 17 -cp "$CP" -d "$OUT/c-$name.jar" $files
  CP="$CP:$OUT/c-$name.jar"
done
if [[ -d $ROOT/adapters/browser/src/main ]]; then
  $KC -jvm-target 17 -cp "$CP" -d "$OUT/adapters-browser.jar" $(find "$ROOT/adapters/browser/src/main" -name '*.kt')
  CP="$CP:$OUT/adapters-browser.jar"
fi
ok=0; fail=0
for eng in "$ROOT"/engines/*/src/main; do
  [[ -d $eng ]] || continue
  name=$(basename $(dirname $eng))
  files=$(find "$eng" -name '*.kt')
  [[ -n $files ]] || continue
  if $KC -jvm-target 17 -cp "$CP" -d "$OUT/e-$name.jar" $files 2>"$OUT/e-$name.err"; then
    echo "OK $name"; ok=$((ok+1))
  else
    echo "FAIL $name"; head -5 "$OUT/e-$name.err"; fail=$((fail+1))
  fi
done
echo "SUMMARY ok=$ok fail=$fail"
