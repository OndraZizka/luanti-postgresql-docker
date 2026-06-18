
FROM lscr.io/linuxserver/luanti:5.16.1 AS luanti-mineclonia

# 0.121.1
ARG minecloniaUrl=https://content.luanti.org/packages/ryvnf/mineclonia/releases/37051/download/

RUN wget -O mineclonia.zip $minecloniaUrl && \
    mkdir -p /config/.minetest/ && \
    unzip mineclonia.zip -d /config/.minetest/games/ && \
    rm mineclonia.zip
