#!/bin/sh
set -e

mkdir -p /app/seed/uploads

DB_PATH="/app/seed/panelsv"

exec java -Dspring.datasource.url=jdbc:sqlite:$DB_PATH -jar $(find /app/target -name "*.jar" | head -n 1)