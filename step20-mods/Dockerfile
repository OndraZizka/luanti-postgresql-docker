

FROM ondrejzizka/luanti-mineclonia

##  ===  Mods - environment - all should be compatible with Mineclonia.  ===

COPY ./downloadAndInstallMod.sh ./


## --- Mobs ---

## Mobs Redo API
RUN ./downloadAndInstallMod.sh TenPlus1/mobs/releases/29659
###  Depends: Wilhelmines Animal World
RUN ./downloadAndInstallMod.sh Liil/animalworld/releases/24027
###    Depends: Mobs Monster
RUN ./downloadAndInstallMod.sh TenPlus1/mobs_monster/releases/29174
###    Depends: Mobs Animal
RUN ./downloadAndInstallMod.sh TenPlus1/mobs_animal/releases/29622

## --- Utilities ---
### Leads
RUN ./downloadAndInstallMod.sh SilverSandstone/leads/releases/23044
### Simple Woodcutter [verified]
RUN ./downloadAndInstallMod.sh luarocks/simple_woodcutter/releases/20594

###  Protector Redo
RUN ./downloadAndInstallMod.sh TenPlus1/protector/releases/29522


## --- Environment ---
### X-Compat [LIBRARY]
RUN ./downloadAndInstallMod.sh mt-mods/xcompat/releases/27623
### More Trees!
RUN ./downloadAndInstallMod.sh mt-mods/moretrees/releases/28466


## Library for Schematics
RUN ./downloadAndInstallMod.sh Sokomine/handle_schematics/releases/5339
### Depends: Villages
RUN ./downloadAndInstallMod.sh Sokomine/mg_villages/releases/20488

## Extra Structures
RUN ./downloadAndInstallMod.sh mt-mods/mcl_extra_structures/releases/18551

## Animalia [WORKS]
RUN ./downloadAndInstallMod.sh ElCeejo/animalia/releases/23715
## Everness [FAILS]
#RUN ./downloadAndInstallMod.sh SaKeL/everness/releases/24216

##  Stone-like Blocks for Mineclonia
RUN ./downloadAndInstallMod.sh dougsouza26/stonelike_blocks/releases/27910

## More Ores
RUN ./downloadAndInstallMod.sh Calinou/moreores/releases/28903

## Better End https://content.luanti.org/packages/TX_Miner/mcl_better_end/
RUN ./downloadAndInstallMod.sh TX_Miner/mcl_better_end/releases/29849


## --- Silly stuff ---

###    AirUtils
RUN ./downloadAndInstallMod.sh apercy/airutils/releases/29726
###    Depends: Steampunk Blimp
RUN ./downloadAndInstallMod.sh apercy/steampunk_blimp/releases/29737




##  === Mods - server administration ===
RUN ./downloadAndInstallMod.sh AntumDeluge/whitelist/releases/14093
### Areas - protection of areas
RUN ./downloadAndInstallMod.sh ShadowNinja/areas/releases/27748
### Simple Protection - area chunk protection, see https://content.luanti.org/packages/Krock/simple_protection/
#RUN ./downloadAndInstallMod.sh Krock/simple_protection/releases/14476
### Funny tweaks of players' physics.
RUN ./downloadAndInstallMod.sh wilkgr76/admin_toys/releases/363


## Edit world.mt in the world's folder to contain load_file_MODNAME = true

ENV LUANTI_BASE_DIR=/config/.minetest
ENV WORLD_DIR=$LUANTI_BASE_DIR/worlds/DemoWorld

## RUN mkdir -p "$WORLD_DIR"
## COPY template_world.mt "$WORLD_DIR/world.mt"
RUN find /config/.minetest/mods/ -type d -maxdepth 1 | sed 's#/#load_file_#g' | sed 's#$# = true#' > enableAllMods-putTo-world.mt


