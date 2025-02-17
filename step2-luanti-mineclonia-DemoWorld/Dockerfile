
FROM ondrejzizka/luanti-mineclonia:latest

ENV LUANTI_BASE_DIR=/config/.minetest
ENV WORLD_DIR=$LUANTI_BASE_DIR/worlds/DemoWorld

#RUN mkdir -p "$WORLD_DIR"
COPY template_world.mt "$WORLD_DIR/world.mt"

#RUN mkdir -p "$LUANTI_BASE_DIR/main-config"
COPY template_main-config_minetest.conf $LUANTI_BASE_DIR/main-config/minetest.conf
