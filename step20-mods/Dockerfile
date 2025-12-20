

FROM ondrejzizka/luanti-mineclonia AS luanti-mineclonia-mods

##  ===  Mods - environment - all should be compatible with Mineclonia.  ===

COPY ./downloadAndInstallMod.sh ./


## --- Mobs ---

## Mobs Redo API 2025-12-05
RUN ./downloadAndInstallMod.sh TenPlus1/mobs/releases/33765
### Depend:
RUN ./downloadAndInstallMod.sh TenPlus1/mobs_monster/releases/33474
RUN ./downloadAndInstallMod.sh TenPlus1/mobs_animal/releases/33409
### Newly added 2025-12-18 - not tested.
#RUN ./downloadAndInstallMod.sh TenPlus1/dmobs/releases/33187
#RUN ./downloadAndInstallMod.sh TenPlus1/mobs_water/releases/33575
#RUN ./downloadAndInstallMod.sh TenPlus1/nssb/releases/30952
#RUN ./downloadAndInstallMod.sh TenPlus1/nssm/releases/33886
#RUN ./downloadAndInstallMod.sh TenPlus1/nssm_extra/releases/33887


###  Depends: Wilhelmines Animal World
RUN ./downloadAndInstallMod.sh Liil/animalworld/releases/24027
### Newly added 2025-12-18 - not tested.
#RUN ./downloadAndInstallMod.sh Liil/livingcavesmobs/releases/26889
#RUN ./downloadAndInstallMod.sh Liil/livingdesert/releases/19250
#RUN ./downloadAndInstallMod.sh Liil/livingfloatlands/releases/19252
#RUN ./downloadAndInstallMod.sh Liil/livingjungle/releases/17267
RUN ./downloadAndInstallMod.sh Liil/livingnether/releases/26157
#RUN ./downloadAndInstallMod.sh Liil/marinaramobs/releases/16924

## Too old? 2021
#RUN ./downloadAndInstallMod.sh DrPlamsa/flower_cow/releases/5998
#RUN ./downloadAndInstallMod.sh DrPlamsa/guinea_pig/releases/5997
## 2025
#RUN ./downloadAndInstallMod.sh TenPlus1/mobs_slimes/releases/30949
#RUN ./downloadAndInstallMod.sh FreeLikeGNU/goblins/releases/32959


## --- Utilities ---

#RUN ./downloadAndInstallMod.sh m16b/m16b_new_elytras/releases/32336

### Leads
RUN ./downloadAndInstallMod.sh SilverSandstone/leads/releases/23044
### Simple Woodcutter [verified]
RUN ./downloadAndInstallMod.sh luarocks/simple_woodcutter/releases/20594

###  Protector Redo
RUN ./downloadAndInstallMod.sh TenPlus1/protector/releases/32440


## --- Environment ---
### X-Compat [LIBRARY]
RUN ./downloadAndInstallMod.sh mt-mods/xcompat/releases/27623
### More Trees!
RUN ./downloadAndInstallMod.sh mt-mods/moretrees/releases/28466

## Other Worlds - asteroids - 2025-06-04
#RUN ./downloadAndInstallMod.sh TenPlus1/other_worlds/releases/31465


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

## Baked Clay
#RUN ./downloadAndInstallMod.sh TenPlus1/bakedclay/releases/31934

## More Ores
RUN ./downloadAndInstallMod.sh Calinou/moreores/releases/30151

## Better End
RUN ./downloadAndInstallMod.sh TX_Miner/mcl_better_end/releases/29849


## --- Silly stuff ---

###    AirUtils
#RUN ./downloadAndInstallMod.sh apercy/airutils/releases/33562
###    Depends: Steampunk Blimp
#RUN ./downloadAndInstallMod.sh apercy/steampunk_blimp/releases/33749




##  === Mods - server administration ===
RUN ./downloadAndInstallMod.sh AntumDeluge/whitelist/releases/14093
### Areas - protection of areas
RUN ./downloadAndInstallMod.sh ShadowNinja/areas/releases/31541
### Simple Protection - area chunk protection, see RUN ./downloadAndInstallMod.sh Krock/simple_protection/
#RUN ./downloadAndInstallMod.sh Krock/simple_protection/releases/14476
### Funny tweaks of players' physics.
RUN ./downloadAndInstallMod.sh wilkgr76/admin_toys/releases/363


## To enable the mods, the `worlds/<name>/world.mt` needs to contain `load_file_<MODNAME> = true` for each.
## This prepares such list.
RUN find /config/.minetest/mods/ -type d -maxdepth 1 | sed 's#/#load_file_#g' | sed 's#$# = true#' > enableAllMods-putTo-world.mt


