FROM lscr.io/linuxserver/luanti:5.17.0 AS luanti-mineclonia

# Ensure the build fails if any RUN command fails
SHELL ["/bin/bash", "-o", "pipefail", "-c"]

# 0.124.0
ARG minecloniaUrl=https://content.luanti.org/packages/ryvnf/mineclonia/releases/39338/download/

RUN wget -O mineclonia.zip $minecloniaUrl && \
    mkdir -p /config/.minetest/ && \
    unzip mineclonia.zip -d /config/.minetest/games/ && \
    rm mineclonia.zip
