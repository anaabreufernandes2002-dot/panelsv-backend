#!/bin/sh
set -e
exec java -jar $(find /app/target -name "*.jar" | head -n 1)