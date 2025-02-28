
FROM ondrejzizka/luanti-mineclonia-demo:latest

ENV LUANTI_BASE_DIR=/config/.minetest
ENV WORLD_DIR=$LUANTI_BASE_DIR/worlds/DemoWorld

RUN cat enableAllMods-putTo-world.mt >> "$WORLD_DIR/world.mt"

RUN src/setValues.sh to-be-applied/minetest.conf $LUANTI_BASE_DIR/main-config/minetest.conf
#COPY src/setValues.sh

