FROM lscr.io/linuxserver/minetest:latest

## This actually needs to be applied in the mounted volume, before booting the server.

#RUN sed -i 's#^backend = .+#backend = postgresql#' /config/.minetest/worlds/world/world.mt


#RUN echo 'pgsql_connection=user=postgres dbname=postgres' >> /config/.minetest/worlds/world/world.mt

RUN wget -O mineclonia.zip https://content.luanti.org/packages/ryvnf/mineclonia/releases/28965/download/
RUN unzip mineclonia.zip -d /downloaded/mineclonia

## TODO: Init script that will copy it to /config/.minetest, where we mount a volume.