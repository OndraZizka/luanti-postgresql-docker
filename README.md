
# Running Luanti (Minetest) in docker with PostgreSQL database.

This is work in progress; with some manual work described below, it works.


## Steps of the build

0. Take a Luanti image
1. Add Mineclonia - `step1-luanti-with-mineclonia`
2. Add some selected mods - `step1-luanti-with-mineclonia`
3. Add a demo world to be able to run from a CLI without a volume - `step2-luanti-mineclonia-DemoWorld`
4. Add an ability to copy a pre-created world dir into a mounted volume.
   * `docker run -v <image> spawn <world-name>`
9. Set up Luanti to use PostgreSQL as a backend. `step3-luanti-with-postgresql`

