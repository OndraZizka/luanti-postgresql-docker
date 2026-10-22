#!/bin/bash
## Wraps the mavenizer-tool plumbing: compiles it, builds the classpath and runs it.
## Usage: mavenize.sh [--local] [--game=author/name] [author/name[/releaseId]]...
## Without --local, packages are deployed to Central (GPG passphrase is prompted for, unless
## MAVEN_GPG_PASSPHRASE is already set).
set -euo pipefail

TOOL_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$TOOL_DIR"

if [[ $# -eq 0 ]]; then
    sed -n '2,5p' "${BASH_SOURCE[0]}" | sed 's/^## \?//'
    exit 1
fi

mvn -q -B compile dependency:build-classpath -Dmdep.outputFile=target/classpath.txt

local_only=false
for a in "$@"; do [[ "$a" == "--local" ]] && local_only=true; done

if ! $local_only && [[ -z "${MAVEN_GPG_PASSPHRASE:-}" ]]; then
    export GPG_TTY=$(tty)
    read -rsp "GPG passphrase: " MAVEN_GPG_PASSPHRASE; export MAVEN_GPG_PASSPHRASE; echo
    trap 'unset MAVEN_GPG_PASSPHRASE' EXIT
fi

java -cp "target/classes:$(cat target/classpath.txt)" ch.zizka.luanti.mavenizer.MainKt "$@"
