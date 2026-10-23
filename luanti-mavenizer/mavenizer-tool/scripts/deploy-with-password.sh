#!/bin/bash

TOOL_DIR="$(cd "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/../.." && pwd)"
cd "$TOOL_DIR/mavenizer-tool/generated-mods-project/tbook/living_villages"
export GPG_TTY=$(tty)
read -rsp "GPG passphrase: " MAVEN_GPG_PASSPHRASE; export MAVEN_GPG_PASSPHRASE; echo
mvn clean deploy -P ossrh
unset MAVEN_GPG_PASSPHRASE
