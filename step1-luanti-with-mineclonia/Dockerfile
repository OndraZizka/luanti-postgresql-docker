
FROM lscr.io/linuxserver/luanti:latest

#RUN wget -O mineclonia.zip https://content.luanti.org/packages/ryvnf/mineclonia/releases/28965/download/

# 0.111.1
RUN wget -O mineclonia.zip https://content.luanti.org/packages/ryvnf/mineclonia/releases/29735/download/ && \
    mkdir -p /config/.minetest/ && \
    unzip mineclonia.zip -d /config/.minetest/games/ && \
    rm mineclonia.zip

## Mods - environment
RUN wget -O mod.zip https://content.luanti.org/packages/ElCeejo/animalia/releases/23715/download/ && unzip mod.zip -d /config/.minetest/mods/ && rm mod.zip
RUN wget -O mod.zip https://content.luanti.org/packages/SaKeL/everness/releases/24216/download/ && unzip mod.zip -d /config/.minetest/mods/ && rm mod.zip

## Mods - server administration
RUN wget -O mod.zip https://content.luanti.org/packages/AntumDeluge/whitelist/releases/14093/download/ && unzip mod.zip -d /config/.minetest/mods/ && rm mod.zip
RUN wget -O mod.zip https://content.luanti.org/packages/ShadowNinja/areas/releases/27748/download/ && unzip mod.zip -d /config/.minetest/mods/ && rm mod.zip
