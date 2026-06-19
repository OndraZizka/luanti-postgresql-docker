
FROM ondrejzizka/luanti-mineclonia-demo:latest AS luanti-mineclonia-zizka

# Ensure the build fails if any RUN command fails
SHELL ["/bin/bash", "-o", "pipefail", "-c"]

ENV LUANTI_BASE_DIR=/config/.minetest
ENV WORLD_DIR=$LUANTI_BASE_DIR/worlds/DemoWorld

COPY src/setValues.sh src/setValues.sh
COPY to-be-applied/minetest.conf to-be-applied/minetest.conf
RUN src/setValues.sh to-be-applied/minetest.conf $LUANTI_BASE_DIR/main-config/minetest.conf
