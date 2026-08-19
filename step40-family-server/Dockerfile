
FROM ondrejzizka/luanti-mineclonia-easy:latest AS luanti-mineclonia-zizka

# Ensure the build fails if any RUN command fails
SHELL ["/bin/bash", "-o", "pipefail", "-c"]

ENV LSIO_LUANTI_BASE_DIR=/config/.minetest
ENV THIS_WORLD_DIR=$LSIO_LUANTI_BASE_DIR/worlds/world

RUN mkdir -p "$THIS_WORLD_DIR" && cp /templates/template_world.mt "$THIS_WORLD_DIR/world.mt"

## Enable all mods for this world which is being prepared.
RUN echo "" >> "$THIS_WORLD_DIR/world.mt"
RUN cat /enableAllMods-putTo-world.mt >> "$THIS_WORLD_DIR/world.mt"

COPY src/setValues.sh src/setValues.sh
COPY to-be-applied/minetest.conf to-be-applied/minetest.conf
RUN src/setValues.sh to-be-applied/minetest.conf $LSIO_LUANTI_BASE_DIR/main-config/minetest.conf

## `worlds/` is normally volume-mounted at runtime, so anything baked into
## $THIS_WORLD_DIR above is invisible once a persistent volume is attached.
## Keep a copy in a path that is NOT under the volume mount, so the
## custom-cont-init.d script below can use it to seed/sync the real world.mt
## on every container start.
RUN mkdir -p /defaults/luanti && cp "$THIS_WORLD_DIR/world.mt" /defaults/luanti/world.mt

COPY custom-cont-init.d/10-sync-luanti-world.sh /custom-cont-init.d/10-sync-luanti-world.sh
RUN chmod +x /custom-cont-init.d/10-sync-luanti-world.sh
