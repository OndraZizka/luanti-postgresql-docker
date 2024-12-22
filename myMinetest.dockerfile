FROM lscr.io/linuxserver/minetest:latest

## This actually needs to be applied in the mounted volume, before booting the server.

#RUN echo 'backend = postgresql' >> /config/.minetest/worlds/world/world.mt
#RUN sed -i 's#^backend = .+#backend = postgresql#' /config/.minetest/worlds/world/world.mt


#RUN echo 'pgsql_connection=user=postgres dbname=postgres' >> /config/.minetest/worlds/world/world.mt

