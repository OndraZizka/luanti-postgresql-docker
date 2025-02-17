
FROM lscr.io/linuxserver/luanti:latest

#RUN wget -O mineclonia.zip https://content.luanti.org/packages/ryvnf/mineclonia/releases/28965/download/

# 0.111.1
RUN wget -O mineclonia.zip https://content.luanti.org/packages/ryvnf/mineclonia/releases/29735/download/
RUN mkdir -p /config/.minetest/
RUN unzip mineclonia.zip -d /config/.minetest/games/
RUN rm mineclonia.zip

RUN wget -O mod-animalia.zip https://content.luanti.org/packages/ElCeejo/animalia/releases/23715/download/
RUN unzip mod-animalia.zip -d /config/.minetest/mods/
RUN rm mod-animalia.zip


RUN wget -O mod-everness.zip https://content.luanti.org/packages/SaKeL/everness/releases/24216/download/
RUN unzip mod-everness.zip -d /config/.minetest/mods/
RUN rm mod-everness.zip
