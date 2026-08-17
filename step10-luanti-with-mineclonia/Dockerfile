FROM lscr.io/linuxserver/luanti:5.16.1 AS luanti-mineclonia

# Ensure the build fails if any RUN command fails
SHELL ["/bin/bash", "-o", "pipefail", "-c"]

# 0.123.0
ARG minecloniaUrl=https://content.luanti.org/packages/ryvnf/mineclonia/releases/37652/download/

RUN wget -O mineclonia.zip $minecloniaUrl && \
    mkdir -p /config/.minetest/ && \
    unzip mineclonia.zip -d /config/.minetest/games/ && \
    rm mineclonia.zip
