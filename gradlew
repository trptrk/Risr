#!/bin/sh
DIR=$(dirname "$0")
command -v gradle >/dev/null 2>&1 || { echo >&2 "Gradle is not installed. Please install Gradle."; exit 1; }
gradle "$@"
