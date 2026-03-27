#!/bin/sh
set -e

mkdir -p /app/data/uploads

JAR=$(find /app/target -name "*.jar" | head -n 1)

echo "Running JAR: $JAR"

exec java -jar "$JAR"