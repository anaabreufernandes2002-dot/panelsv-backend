#!/bin/sh
set -e

mkdir -p /app/data/uploads

if [ ! -f /app/data/panelsv.db ] && [ -f /app/seed/panelsv ]; then
  cp /app/seed/panelsv /app/data/panelsv.db
fi

if [ -d /app/seed/uploads ] && [ -z "$(ls -A /app/data/uploads 2>/dev/null)" ]; then
  cp -R /app/seed/uploads/. /app/data/uploads/
fi

JAR=$(find /app/target -maxdepth 1 -name "*.jar" | head -n 1)
exec java -jar "$JAR"