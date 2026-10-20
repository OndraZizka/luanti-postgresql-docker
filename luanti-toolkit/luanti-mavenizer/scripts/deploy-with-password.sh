#!/bin/bash

cd /home/o/uw/luanti-mavenizer/mavenizer-tool/generated-mods-project/tbook/living_villages
export GPG_TTY=$(tty)
read -rsp "GPG passphrase: " GPG_PASSPHRASE; export GPG_PASSPHRASE; echo
mvn clean deploy -P ossrh
unset GPG_PASSPHRASE