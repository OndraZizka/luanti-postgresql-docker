#!/bin/bash
## Mavenizes and deploys (mvn deploy -P ossrh, GPG-signed) the latest release of ALL mods supporting Mineclonia
## (ContentDB: game=ryvnf/mineclonia, ~330 mods). Pass --local to only install into ~/.m2 instead.
set -euo pipefail

HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$HERE/../mavenizer-tool"
mvn -q -B compile dependency:build-classpath -Dmdep.outputFile=target/classpath.txt

if [[ "${1:-}" != "--local" ]]; then
    export GPG_TTY=$(tty)
    read -rsp "GPG passphrase: " MAVEN_GPG_PASSPHRASE; export MAVEN_GPG_PASSPHRASE; echo
    trap 'unset MAVEN_GPG_PASSPHRASE' EXIT
fi

java -cp "target/classes:$(cat target/classpath.txt)" ch.zizka.luanti.mavenizer.MainKt \
    "$@" --game=ryvnf/mineclonia | tee "$HERE/deploy-mineclonia-mods.log"

echo "Failures: grep -E '^(Failed|Could not|No )' $HERE/deploy-mineclonia-mods.log"
