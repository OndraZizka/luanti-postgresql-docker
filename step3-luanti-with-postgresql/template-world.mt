enable_damage = true
creative_mode = false
mod_storage_backend = sqlite3
auth_backend = sqlite3
player_backend = sqlite3

backend = postgresql
gameid = mineclonia
world_name = world3

## This way, Luanti tries to connect to a socket: /run/postgresql/.s.PGSQL.5432
#pgsql_connection = user=postgres dbname=postgres

## This directs it to the given TCP connection.
pgsql_connection = host=minetest-postgres port=5432 user=postgres password=postgres dbname=postgres

## TODO find out what we can have.
#default_privs = interact, shout, home


## interact Allows the player to interact with the world (e.g., place and break nodes).
## fly Allows the player to fly around the world.
## fast Allows the player to move faster than usual.
## noclip Allows the player to pass through walls and other solid objects.
## shout Allows the player to send chat messages that everyone can hear, regardless of their position in the world.
## teleport Allows the player to teleport to any position in the world.
## home Allows the player to set and teleport to a home position.
## privs Allows the player to manage privileges for other players.
## fly Grants the player the ability to fly.
## worldedit Allows the player to use the WorldEdit mod (if installed).
## build Allows the player to build and destroy objects in protected areas.
## settime Allows the player to set the time of day in the world.
## ban Allows the player to ban other players.
## kick Allows the player to kick other players from the server.
## server Gives the player access to server commands and configurations.
## give Allows the player to give themselves items.
## teleport Allows the player to teleport to other players or locations.
## admin Grants the player full administrative access to the server, allowing them to manage players, the world, and configuration.