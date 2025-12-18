
FROM ondrejzizka/luanti-mineclonia-demo:latest AS luanti-mineclonia-zizka

ENV LUANTI_BASE_DIR=/config/.minetest
ENV WORLD_DIR=$LUANTI_BASE_DIR/worlds/DemoWorld

COPY src/setValues.sh src/setValues.sh
COPY to-be-applied/minetest.conf to-be-applied/minetest.conf
RUN src/setValues.sh to-be-applied/minetest.conf $LUANTI_BASE_DIR/main-config/minetest.conf
