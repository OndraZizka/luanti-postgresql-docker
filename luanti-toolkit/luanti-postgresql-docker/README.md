
# Running Luanti (Minetest) in docker with PostgreSQL database.

This is work in progress; with some manual work described below, it works.


## Steps of the build

0. Take a Luanti image
1. Add Mineclonia - `step10-luanti-with-mineclonia`
2. Add some selected mods - `step10-luanti-with-mineclonia`
3. Add a demo world to be able to run from a CLI without a volume - `step20-luanti-mineclonia-DemoWorld`
4. Add an ability to copy a pre-created world dir into a mounted volume.
   * `docker run -v <image> spawn <world-name>`
9. Set up Luanti to use PostgreSQL as a backend. `step30-luanti-with-postgresql`



## Maintenance of the server

1) Keep the mods limited. They can kill the server completely.
2) Sometimes run: 
    ```bash
    sqlite3 /config/.minetest/worlds/world/map.sqlite "VACUUM;"
    sqlite3 /config/.minetest/worlds/world/mod_storage.sqlite "VACUUM;"
    sqlite3 /config/.minetest/worlds/world/players.sqlite "VACUUM;"
    ```
3) Try to run this several times until it doesn't fail and starts recompressing:
   ```
   pkill luantiserver; /usr/bin/luantiserver --recompress --world /config/.minetest/worlds/world
   ```