
FROM ondrejzizka/luanti-mineclonia-easy:latest AS luanti-mineclonia-demo

## Ensure the build fails if any RUN command fails
SHELL ["/bin/bash", "-o", "pipefail", "-c"]

#ENV LSIO_LUANTI_BASE_DIR=/config/.minetest
ENV THIS_WORLD_DIR=$LSIO_LUANTI_BASE_DIR/worlds/DemoWorld

RUN mkdir -p "$THIS_WORLD_DIR" && cp /templates/template_world.mt "$THIS_WORLD_DIR/world.mt"

## Enable all mods for this world which is being prepared.
RUN echo "" >> "$THIS_WORLD_DIR/world.mt"
RUN cat /enableAllMods-putTo-world.mt >> "$THIS_WORLD_DIR/world.mt"
