#!/usr/bin/env bash


NEW_PROPERTIES_FILE="${1:-../to-be-applied/minetest.conf}"
#NEW_PROPERTIES_FILE="../to-be-applied/minetest.conf"

#TARGET_CONFIG_FILE=./minetest-data/main-config/minetest.conf
TARGET_CONFIG_FILE="$2"

#if [ ! -f $TARGET_CONFIG_FILE ] ; then echo "Not found $TARGET_CONFIG_FILE"; exit 1; fi
if [ -d $TARGET_CONFIG_FILE ] ; then echo "It's a directory: $TARGET_CONFIG_FILE"; exit 1; fi

while IFS= read -r LINE; do
    #if [[ $LINE =~ ^#.* ]] ; then continue; fi
    if [[ $LINE = \#* ]] ; then continue; fi
    #if [[ $LINE =~ " \*" ]] ; then continue; fi
    #if [[ -n "${LINE/ //}" ]] ; then continue; fi
    if [[ $LINE = "" ]] ; then continue; fi


    echo -e "\n\tSetting: $LINE"

    PROPERTY_NAME=$(echo "$LINE" | sed 's# \?=.\+##g')
    PROPERTY_VALUE=$(echo "$LINE" | sed 's#^.* *= *##')
    echo -e "\tProperty: $PROPERTY_NAME: $PROPERTY_VALUE"

    sed -i '/^$PROPERTY_NAME/d' "$TARGET_CONFIG_FILE"
    echo "$PROPERTY_NAME = $PROPERTY_VALUE" >> "$TARGET_CONFIG_FILE"

done < $NEW_PROPERTIES_FILE