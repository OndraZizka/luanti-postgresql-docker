
FROM lscr.io/linuxserver/luanti:latest AS luanti-mineclonia

#RUN wget -O mineclonia.zip https://content.luanti.org/packages/ryvnf/mineclonia/releases/28965/download/

# 0.111.1
RUN wget -O mineclonia.zip https://content.luanti.org/packages/ryvnf/mineclonia/releases/29735/download/ && \
    mkdir -p /config/.minetest/ && \
    unzip mineclonia.zip -d /config/.minetest/games/ && \
    rm mineclonia.zip
