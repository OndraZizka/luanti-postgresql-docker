FROM ondrejzizka/luanti-mineclonia:latest

# Ensure the build fails if any RUN command fails
SHELL ["/bin/bash", "-o", "pipefail", "-c"]

## This actually needs to be applied in the mounted volume, before booting the server.
#RUN sed -i 's#^backend = .+#backend = postgresql#' /config/.minetest/worlds/world/world.mt
#RUN echo 'pgsql_connection=user=postgres dbname=postgres' >> /config/.minetest/worlds/world/world.mt
