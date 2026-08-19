
set -euo pipefail

MOD_AUTHOR_AND_NAME_AND_RELEASE="$1"
MOD_NAME=$(echo "$MOD_AUTHOR_AND_NAME_AND_RELEASE" | cut -d/ -f2)

URL="https://content.luanti.org/packages/$MOD_AUTHOR_AND_NAME_AND_RELEASE/download/"

WORKDIR=$(mktemp -d)
mkdir -p "$WORKDIR/mod"

echo " * Downloading: $URL"
curl -fL -o "$WORKDIR/mod.zip" "$URL" || { echo "ERROR: Failed to download from $URL (HTTP error or file not found - check 404)"; exit 1; }
unzip -q "$WORKDIR/mod.zip" -d "$WORKDIR/mod/" || { echo "ERROR: Failed to unzip mod.zip"; exit 1; }
rm "$WORKDIR/mod.zip"

TARGET_DIR="/config/.minetest/mods/$MOD_NAME"
mkdir -p "$(dirname "$TARGET_DIR")"

## Count entries INSIDE the extracted zip (not the script's own cwd!).
ENTRY_COUNT=$(find "$WORKDIR/mod" -mindepth 1 -maxdepth 1 | wc -l)
FIRST_ENTRY=$(find "$WORKDIR/mod" -mindepth 1 -maxdepth 1)

if [ "$ENTRY_COUNT" -eq 1 ] && [ -d "$FIRST_ENTRY" ]; then
    ## Zip contains a single wrapping directory (the common case for
    ## content.luanti.org releases) - use its content directly, so mod.conf
    ## and init.lua end up right under mods/$MOD_NAME/, as Luanti expects.
    mv "$FIRST_ENTRY" "$TARGET_DIR"
else
    ## Zip content is already flat (no wrapping directory).
    echo "   WARN Mod is not zipped in a root directory, will extract to .../$MOD_NAME/ ."
    mv "$WORKDIR/mod" "$TARGET_DIR"
fi

rm -rf "$WORKDIR"
