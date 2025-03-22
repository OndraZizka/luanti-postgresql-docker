
FROM lscr.io/linuxserver/luanti:latest AS luanti-mineclonia

# 0.113.0
RUN wget -O mineclonia.zip https://content.luanti.org/packages/ryvnf/mineclonia/releases/30146/download/ && \
    mkdir -p /config/.minetest/ && \
    unzip mineclonia.zip -d /config/.minetest/games/ && \
    rm mineclonia.zip
