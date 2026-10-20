#!/usr/bin/with-contenv bash
# shellcheck shell=bash
#
# `worlds/` is typically volume-mounted for persistence, so anything baked
# into worlds/world/world.mt at image build time is invisible at runtime -
# Docker never merges image content into an already-populated bind mount.
#
# This script runs on every container start (LSIO custom-cont-init.d hook)
# and keeps the persisted world.mt in sync with the mods actually shipped
# in this image:
#   - First run (no world.mt yet): seed it from the full baked-in default.
#   - Subsequent runs:
#     - ADD any missing `load_mod_*` lines. Never removes or overwrites
#       OTHER lines, so manual edits/privileges survive restarts/updates.
#     - FIX any existing `load_mod_<name> = mods/<name>` lines left over
#       from an earlier bug: Luanti only treats `load_mod_<name> = true` as
#       "enabled" - a path value is silently ignored, so mods installed
#       that way never actually load despite the line being present.

set -euo pipefail

WORLD_DIR="/config/.minetest/worlds/world"
WORLD_MT="$WORLD_DIR/world.mt"
DEFAULT_WORLD_MT="/defaults/luanti/world.mt"
MODS_LIST="/enableAllMods-putTo-world.mt"

mkdir -p "$WORLD_DIR"

if [[ ! -f "$WORLD_MT" ]]; then
    echo "[sync-luanti-world] No world.mt found at $WORLD_MT - seeding from image defaults."
    cp "$DEFAULT_WORLD_MT" "$WORLD_MT"
    exit 0
fi

## Fix any old `load_mod_<name> = mods/<name>` lines to `= true`, in place.
if grep -qE '^load_mod_[^ ]+ *= *mods/' "$WORLD_MT"; then
    echo "[sync-luanti-world] Fixing legacy load_mod_* lines using 'mods/<name>' instead of 'true'."
    sed -i -E 's#^(load_mod_[^ ]+) *= *mods/.*#\1 = true#' "$WORLD_MT"
fi

echo "[sync-luanti-world] Syncing missing load_mod_* entries into existing $WORLD_MT"
while IFS= read -r LINE; do
    [[ -z "$LINE" ]] && continue
    KEY="${LINE%% =*}"
    if ! grep -q "^${KEY} *=" "$WORLD_MT"; then
        echo "[sync-luanti-world] Adding missing entry: $LINE"
        echo "$LINE" >> "$WORLD_MT"
    fi
done < "$MODS_LIST"
