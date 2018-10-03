#!/bin/sh
set -e

echo "JAVA_OPTS: $JAVA_OPTS"

/usr/bin/java $JAVA_OPTS -jar /usr/share/myservice/myservice.jar

exec "$@"
