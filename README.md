
# Running Luanti (Minetest) in docker with PostgreSQL database.

This is work in progress; with some manual work described below, it works.


## PostgreSQL
Postgres runs as user 999
PostgreSQL in Docker, on a mounted volume, creates the files owned by userID / groupID 999 (in postgres container, it's `postgres:postgres`).
```
$ docker run --rm postgres:17 id postgres
uid=999(postgres) gid=999(postgres) groups=999(postgres),101(ssl-cert)
```

(Typical Ubuntu setup has the 1st user ID = 1000.)

So the dir may be (re-)created like this:

```
sudo rm -rf postgres-data/
mkdir postgres-data/
sudo chown $USER:999 postgres-data/
sudo chmod g+rwx postgres-data/
```
TODO: Automate this.

## Installing Mineclonia
```
rm -rf ./minetest-data/

if [ ! -d minetest-data/games/mineclonia/ ] ; then
    mkdir -p instance-data/minetest-data/games/
    wget -O mineclonia.zip https://content.luanti.org/packages/ryvnf/mineclonia/releases/28965/download/
    unzip mineclonia.zip -d minetest-data/games/
    rm mineclonia.zip
fi
```
TODO: Automate this.

## Setting up the server info

```
CONFIG_FILE=./minetest-data/main-config/minetest.conf

sed -i '/^server_announce/d' "$CONFIG_FILE"
sed -i '/^server_name/d' "$CONFIG_FILE"
sed -i '/^server_description/d' "$CONFIG_FILE"
sed -i '/^default_game/d' "$CONFIG_FILE"

echo "server_announce = true" >> "$CONFIG_FILE"
echo "server_name = My Mineclonia Server" >> "$CONFIG_FILE"
echo "server_description = A Mineclonia server powered by Minetest" >> "$CONFIG_FILE"
echo "default_game = Mineclonia" >> "$CONFIG_FILE"
```

## Running Luanti with Mineclonia backed by PostgreSQL

After all above is done (currently not automated), run:

```
docker compose up
```

PostgreSQL should start, Luanti should connect to it, and start saving blocks to the DB.