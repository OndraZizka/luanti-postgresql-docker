

MOD_AUTHOR_AND_NAME_AND_RELEASE="$1"

URL="https://content.luanti.org/packages/$MOD_AUTHOR_AND_NAME_AND_RELEASE/download/"

mkdir ./mod

echo " * Downloading: $URL"
wget -O mod.zip "$URL"
unzip mod.zip -d ./mod/
rm mod.zip

FOO=$( ls -ld * | wc -l)
if [ $FOO == 1 ] ; then
    mv ./mod/ /config/.minetest/mods/
else
    MOD_NAME=`echo "$MOD_AUTHOR_AND_NAME_AND_RELEASE" | cut -d/ -f2`
    echo "   WARN Mod is not zipped in a root directory, will extract to .../$MOD_NAME/ ."
    mkdir -p /config/.minetest/mods/$MOD_NAME/
    mv ./mod/ /config/.minetest/mods/$MOD_NAME/
fi

