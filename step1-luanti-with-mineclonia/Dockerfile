
FROM lscr.io/linuxserver/luanti:latest

#RUN wget -O mineclonia.zip https://content.luanti.org/packages/ryvnf/mineclonia/releases/28965/download/

# 0.111.1
RUN wget -O mineclonia.zip https://content.luanti.org/packages/ryvnf/mineclonia/releases/29735/download/ && \
    mkdir -p /config/.minetest/ && \
    unzip mineclonia.zip -d /config/.minetest/games/ && \
    rm mineclonia.zip


##  ===  Mods - environment - all should be compatible with Mineclonia.  ===

COPY downloadAndInstallMod.sh ./


## --- Mobs ---

## Mobs Redo API
RUN wget -O mod.zip https://content.luanti.org/packages/TenPlus1/mobs/releases/29659/download/ && unzip mod.zip -d /config/.minetest/mods/ && rm mod.zip
###  Depends: Wilhelmines Animal World
RUN wget -O mod.zip https://content.luanti.org/packages/Liil/animalworld/releases/24027/download/ && unzip mod.zip -d /config/.minetest/mods/ && rm mod.zip
###    Depends: Mobs Monster
RUN wget -O mod.zip https://content.luanti.org/packages/TenPlus1/mobs_monster/releases/29174/download/ && unzip mod.zip -d /config/.minetest/mods/ && rm mod.zip
###    Depends: Mobs Animal
RUN wget -O mod.zip https://content.luanti.org/packages/TenPlus1/mobs_animal/releases/29622/download/ && unzip mod.zip -d /config/.minetest/mods/ && rm mod.zip

## --- Utilities ---
### Leads
RUN wget -O mod.zip https://content.luanti.org/packages/SilverSandstone/leads/releases/23044/download/ && unzip mod.zip -d /config/.minetest/mods/ && rm mod.zip
### Simple Woodcutter [verified]
RUN wget -O mod.zip https://content.luanti.org/packages/luarocks/simple_woodcutter/releases/20594/download/ && unzip mod.zip -d /config/.minetest/mods/ && rm mod.zip


## --- Environment ---
### X-Compat [LIBRARY]
RUN wget -O mod.zip https://content.luanti.org/packages/mt-mods/xcompat/releases/27623/download/ && unzip mod.zip -d /config/.minetest/mods/ && rm mod.zip
### More Trees!
RUN wget -O mod.zip https://content.luanti.org/packages/mt-mods/moretrees/releases/28466/download/ && unzip mod.zip -d /config/.minetest/mods/ && rm mod.zip


## Library for Schematics
RUN wget -O mod.zip https://content.luanti.org/packages/Sokomine/handle_schematics/releases/5339/download/ && unzip mod.zip -d /config/.minetest/mods/ && rm mod.zip
### Depends: Villages \
RUN wget -O mod.zip https://content.luanti.org/packages/Sokomine/mg_villages/releases/20488/download/ && unzip mod.zip -d /config/.minetest/mods/ && rm mod.zip

## Extra Structures
RUN wget -O mod.zip https://content.luanti.org/packages/mt-mods/mcl_extra_structures/releases/18551/download/ && unzip mod.zip -d /config/.minetest/mods/ && rm mod.zip

## Animalia [WORKS]
RUN wget -O mod.zip https://content.luanti.org/packages/ElCeejo/animalia/releases/23715/download/ && unzip mod.zip -d /config/.minetest/mods/ && rm mod.zip
## Everness [FAILS]
#RUN wget -O mod.zip https://content.luanti.org/packages/SaKeL/everness/releases/24216/download/ && unzip mod.zip -d /config/.minetest/mods/ && rm mod.zip

##  Stone-like Blocks for Mineclonia
RUN ./downloadAndInstallMod.sh dougsouza26/stonelike_blocks/releases/27910

## More Ores
RUN wget -O mod.zip https://content.luanti.org/packages/Calinou/moreores/releases/28903/download/ && unzip mod.zip -d /config/.minetest/mods/ && rm mod.zip

## Better End https://content.luanti.org/packages/TX_Miner/mcl_better_end/
RUN wget -O mod.zip https://content.luanti.org/packages/TX_Miner/mcl_better_end/releases/29849/download/ && unzip mod.zip -d /config/.minetest/mods/ && rm mod.zip


## --- Silly stuff ---

###    AirUtils
RUN wget -O mod.zip https://content.luanti.org/packages/apercy/airutils/releases/29726/download/ && unzip mod.zip -d /config/.minetest/mods/ && rm mod.zip
###    Depends: Steampunk Blimp
RUN wget -O mod.zip https://content.luanti.org/packages/apercy/steampunk_blimp/releases/29737/download/ && unzip mod.zip -d /config/.minetest/mods/ && rm mod.zip




##  === Mods - server administration ===
RUN wget -O mod.zip https://content.luanti.org/packages/AntumDeluge/whitelist/releases/14093/download/ && unzip mod.zip -d /config/.minetest/mods/ && rm mod.zip
RUN wget -O mod.zip https://content.luanti.org/packages/ShadowNinja/areas/releases/27748/download/ && unzip mod.zip -d /config/.minetest/mods/ && rm mod.zip
