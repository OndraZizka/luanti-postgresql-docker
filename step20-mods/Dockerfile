FROM ondrejzizka/luanti-mineclonia AS luanti-mineclonia-mods

## Ensure the build fails if any RUN command fails
SHELL ["/bin/bash", "-o", "pipefail", "-c"]

##  ===  Mods - environment - all should be compatible with Mineclonia.  ===

COPY ./downloadAndInstallMod.sh ./


## --- Mobs ---

## Mobs Redo API 2025-12-05
## 2025-06-19: Are all these redundant and in fact harmful?
## "Mineclonia features native, custom physics, pathfinding algorithms, and behaviors for its animals and monsters."
## All supposedly for the Minetest game; Mineclonia has its own. Safe to REMOVE.
#RUN ./downloadAndInstallMod.sh TenPlus1/mobs/releases/36381
### Depend:
#RUN ./downloadAndInstallMod.sh TenPlus1/mobs_monster/releases/36382
#RUN ./downloadAndInstallMod.sh TenPlus1/mobs_animal/releases/36266
### Newly added 2025-12-18.
#RUN ./downloadAndInstallMod.sh TenPlus1/dmobs/releases/36326
#RUN ./downloadAndInstallMod.sh TenPlus1/mobs_water/releases/33827
#RUN ./downloadAndInstallMod.sh TenPlus1/nssb/releases/35638
#RUN ./downloadAndInstallMod.sh TenPlus1/nssm/releases/36054
#RUN ./downloadAndInstallMod.sh TenPlus1/nssm_extra/releases/36055


###  Depends: Wilhelmines Animal World
#RUN ./downloadAndInstallMod.sh Liil/animalworld/releases/24027
### Newly added 2025-12-18.
#RUN ./downloadAndInstallMod.sh Liil/livingcavesmobs/releases/27187
#RUN ./downloadAndInstallMod.sh Liil/livingdesert/releases/19548
#RUN ./downloadAndInstallMod.sh Liil/livingfloatlands/releases/19550
#RUN ./downloadAndInstallMod.sh Liil/livingjungle/releases/17565
#RUN ./downloadAndInstallMod.sh Liil/livingnether/releases/26157
#RUN ./downloadAndInstallMod.sh Liil/marinaramobs/releases/17222

## Too old? 2021
#RUN ./downloadAndInstallMod.sh DrPlamsa/flower_cow/releases/6179
#RUN ./downloadAndInstallMod.sh DrPlamsa/guinea_pig/releases/6178
## 2025
#RUN ./downloadAndInstallMod.sh TenPlus1/mobs_slimes/releases/36267
#RUN ./downloadAndInstallMod.sh FreeLikeGNU/goblins/releases/33305


## --- Utilities ---

#RUN ./downloadAndInstallMod.sh m16b/m16b_new_elytras/releases/32816

### Leads [Already included in Mineclonia.]
#RUN ./downloadAndInstallMod.sh SilverSandstone/leads/releases/31637
### Simple Woodcutter [incompatible with Mineclonia]
#RUN ./downloadAndInstallMod.sh luarocks/simple_woodcutter/releases/20594


## --- Environment ---
### X-Compat [LIBRARY]
RUN ./downloadAndInstallMod.sh mt-mods/xcompat/releases/35043
### More Trees!
#RUN ./downloadAndInstallMod.sh mt-mods/moretrees/releases/34573

## Other Worlds - asteroids - 2025-06-04
#RUN ./downloadAndInstallMod.sh TenPlus1/other_worlds/releases/35854


## Library for Schematics [for Minetest -> Incompatible + Redundant - safe to REMOVE]
#RUN ./downloadAndInstallMod.sh Sokomine/handle_schematics/releases/5339
### Depends: Villages [for Minetest -> Incompatible + Redundant - safe to REMOVE]
#RUN ./downloadAndInstallMod.sh Sokomine/mg_villages/releases/20488

## Extra Structures  [for Mineclonia]
RUN ./downloadAndInstallMod.sh mt-mods/mcl_extra_structures/releases/34000

## Animalia [WORKS with Mineclonia]
RUN ./downloadAndInstallMod.sh ElCeejo/animalia/releases/34128
## Everness  [for Minetest -> Incompatible + Redundant, FAILS]
#RUN ./downloadAndInstallMod.sh SaKeL/everness/releases/30981

##  Stone-like Blocks [for Mineclonia]  ## Does not exist anymore?
#RUN ./downloadAndInstallMod.sh dougsouza26/stonelike_blocks/releases/27910

## Baked Clay  [for Minetest -> Incompatible + Redundant]
#RUN ./downloadAndInstallMod.sh TenPlus1/bakedclay/releases/36240

## More Ores
RUN ./downloadAndInstallMod.sh Calinou/moreores/releases/30151

## Better End [for Mineclonia]
RUN ./downloadAndInstallMod.sh TX_Miner/mcl_better_end/releases/29849


## --- Silly stuff ---

###    AirUtils
#RUN ./downloadAndInstallMod.sh apercy/airutils/releases/35858
###    Depends: Steampunk Blimp
#RUN ./downloadAndInstallMod.sh apercy/steampunk_blimp/releases/36216

## -- Added 2026-06 --
## Ambience (Immersive Sounds for Mineclonia)
RUN ./downloadAndInstallMod.sh TenPlus1/ambience/releases/38020
## Mineclonia Furniture & Decorations
RUN ./downloadAndInstallMod.sh rudzik8/mcl_decor/releases/35460
## Mineclonia Wieldlight (Dynamic lighting, off-hand compatible.) May cause server perf issues.
#RUN ./downloadAndInstallMod.sh cora/mcl_wieldlight/releases/25501
## Better - client-only.
RUN ./downloadAndInstallMod.sh mt-mods/illumination/releases/36882
RUN ./downloadAndInstallMod.sh erlehmann/mcl_quick_harvest_replant/releases/20784
#RUN ./downloadAndInstallMod.sh Codiac/mcl_3d_plants/releases/25501  ## Does not exists? Instead:
RUN ./downloadAndInstallMod.sh Codiac/mcl_small_3d_plants/releases/35229
RUN ./downloadAndInstallMod.sh Impulse/enchantments_extractor/releases/22596

## /custom_spawn set: Sets the players (the one who ran the command) spawn to their current position.
## /custom_spawn clear: Clears the players custom spawn, making them use beds / world spawn again.
RUN ./downloadAndInstallMod.sh aliaslion/custom_spawn/releases/36811
RUN ./downloadAndInstallMod.sh camelia/farmtools/releases/32877
RUN ./downloadAndInstallMod.sh Codiac/mcla_codihacks/releases/34713  ## !!! Many changes, could cause troubles !!!
RUN ./downloadAndInstallMod.sh dougsouza26/jds_hollow_logs/releases/35648
## Creepers run away from wild foxes.
## Smelt rotten flesh to leather.
RUN ./downloadAndInstallMod.sh cathisnothome/ofcath_flesctoleather/releases/36653
## Decorative wooden blocks.
RUN ./downloadAndInstallMod.sh dougsouza26/mcl_mosaic_planks/releases/29126
## Decorative pedestal for items.
RUN ./downloadAndInstallMod.sh m16b/m16b_item_displayed/releases/32425
## 3 more decorations of elytra.
RUN ./downloadAndInstallMod.sh m16b/m16b_new_elytras/releases/32336

RUN ./downloadAndInstallMod.sh bgstack15/obsidian_extra/releases/31577
RUN ./downloadAndInstallMod.sh bgstack15/mcl_ruby_stuff/releases/35536
RUN ./downloadAndInstallMod.sh bgstack15/mcl_lapis_stuff/releases/35551
RUN ./downloadAndInstallMod.sh MatthiasZM96/mcl_backpack/releases/23507
## A couple of things, especially, pale oak (white wood).
RUN ./downloadAndInstallMod.sh m16b/m16b_new_mobs/releases/36006
## A boat for lava.
RUN ./downloadAndInstallMod.sh bgstack15/molten_sailor_mcl/releases/35447
## Some more music discs.
RUN ./downloadAndInstallMod.sh cora/mcla_goldberg/releases/24717
RUN ./downloadAndInstallMod.sh Thisaccountis42/mcl_jukebox_c418/releases/16485
RUN ./downloadAndInstallMod.sh TomCon/discovery_maps/releases/34183
## TODO: Set mcl_signs_editable = 1 or such.
RUN ./downloadAndInstallMod.sh nandology/copper_golem/releases/37254
RUN ./downloadAndInstallMod.sh xyzshantaram/vl_copper_rails_recipes/releases/35438


##  === Mods - server administration ===
#RUN ./downloadAndInstallMod.sh ughy/whitelist/releases/35420 ## Not really useful - we want to be open.

###  Protector Redo - .
RUN ./downloadAndInstallMod.sh TenPlus1/protector/releases/37670

### Areas - protection of areas
RUN ./downloadAndInstallMod.sh ShadowNinja/areas/releases/37877

### Simple Protection - area chunk protection, see RUN ./downloadAndInstallMod.sh Krock/simple_protection/
#RUN ./downloadAndInstallMod.sh Krock/simple_protection/releases/14723
### Funny tweaks of players' physics.
RUN ./downloadAndInstallMod.sh wilkgr76/admin_toys/releases/363 \

### A Luanti mod which aims to improve user experience with Area Protection Mod by adding tools for easier area manipulation.
RUN ./downloadAndInstallMod.sh rangeTonic/areas_helper_tools/releases/34790
RUN ./downloadAndInstallMod.sh cheapie/areasprotector/releases/36144


## To enable the mods, the `worlds/<name>/world.mt` needs to contain `load_file_<MODNAME> = true` for each.
## This prepares such list.
RUN find /config/.minetest/mods/ -type d -maxdepth 1 | sed 's#/#load_file_#g' | sed 's#$# = true#' > enableAllMods-putTo-world.mt
