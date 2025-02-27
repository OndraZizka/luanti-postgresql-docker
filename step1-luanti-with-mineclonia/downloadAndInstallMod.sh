

MOD_AUTHOR_AND_NAME_AND_RELEASE="$1"

URL="https://content.luanti.org/packages/$MOD_AUTHOR_AND_NAME_AND_RELEASE/download/"

wget -O mod.zip "$URL" && unzip mod.zip -d /config/.minetest/mods/ && rm mod.zip