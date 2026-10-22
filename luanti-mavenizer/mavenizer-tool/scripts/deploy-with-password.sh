#!/bin/bash

cd /home/o/uw/luanti-toolkit/luanti-mavenizer/mavenizer-tool/generated-mods-project/tbook/living_villages
export GPG_TTY=$(tty)
read -rsp "GPG passphrase: " MAVEN_GPG_PASSPHRASE; export MAVEN_GPG_PASSPHRASE; echo
mvn clean deploy -P ossrh
unset MAVEN_GPG_PASSPHRASE