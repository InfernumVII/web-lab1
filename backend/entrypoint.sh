#!/bin/sh

java -jar /fcgi/fcgi.jar &

JAVA_PID=$!

tail -F /fcgi/logs/log.txt &

wait $JAVA_PID