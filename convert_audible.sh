#!/bin/bash

if [ -z "$AUDIBLE_ACTIVATION_BYTES" ]; then
    echo "AUDIBLE_ACTIVATION_BYTES is not set"
    exit 1
fi

ffmpeg -y \
    -activation_bytes "$AUDIBLE_ACTIVATION_BYTES" \
    -i $1 \
    -map_metadata 0 \
    -id3v2_version 3 \
    -codec:a copy -vn \
    $1.m4b
