
FROM lscr.io/linuxserver/luanti:latest AS luanti-mineclonia

# 0.118.1
ARG minecloniaUrl=https://content.luanti.org/packages/ryvnf/mineclonia/releases/33876/download/
RUN wget -O mineclonia.zip $minecloniaUrl && \
    mkdir -p /config/.minetest/ && \
    unzip mineclonia.zip -d /config/.minetest/games/ && \
    rm mineclonia.zip
