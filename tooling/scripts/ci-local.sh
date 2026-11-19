#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
export JAVA_OPTS="${JAVA_OPTS:--Xmx2g -XX:MaxMetaspaceSize=512m}"
chmod +x gradlew 2>/dev/null || true
./gradlew verifyModuleBuilds printModules
./gradlew :shared:kernel:compileKotlin \
  :shared:engine:terminal:compileKotlin \
  :shared:engine:process:compileKotlin \
  :shared:engine:vault:compileKotlin \
  :shared:engine:tools:compileKotlin \
  :shared:engine:forge:compileKotlin
./gradlew :shared:kernel:test --continue
echo "ci-local finished"
