#!/bin/sh

set -e

JAR=$(find $PWD/build/libs -name "*.jar" -not -name "*source*")
cp -u $JAR $1/
