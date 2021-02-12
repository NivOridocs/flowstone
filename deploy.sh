#!/bin/sh

TARGET=$1

if [ ! $TARGET ]; then
  echo "Error: Missing argument"
  exit 1
fi

LOCAL="$(pwd)/build/libs"
FILE="$(pwd)/gradle.properties"

eval $(cat $FILE | grep '=' | cut -d'#' -f1 | sed 's/^[[:space:]]//g;s/[[:space:]]$//g;' | {
  while IFS='=' read -r key value; do
    key=$(echo $key | sed 's/\./_/g;s/^[[:space:]]//g;s/[[:space:]]$//g;')
    value=$(echo $value | sed 's/^[[:space:]]//g;s/[[:space:]]$//g;')
    echo $key=$value
  done
})

ARCHIVE="${archives_base_name}-${mod_version}.jar"

rm -rf $TARGET/$ARCHIVE
cp -u $LOCAL/$ARCHIVE $TARGET/$ARCHIVE