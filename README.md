
Jelikož tento počin zas na několik týdnů opustím a zapomenu detaily:

PostgreSQL vytvoří soubory ve volume pod uživatelem, pod kterým se poprvé spustí.

Aplikuje na ně user ID z kontejneru, tedy co je v `USER` v Dockerfile (dal jsem tam `postgres`).

Můj user ID = 1000

Asi má postgres také user ID = 1000?

Stejně se do DB ale nedá přihlásit...

`postgres` image má default user `postgres`:

```
$ docker run --rm postgres:17 id postgres
uid=999(postgres) gid=999(postgres) groups=999(postgres),101(ssl-cert)
```

So the dir may be re-created like this:

```
sudo rm -rf postgres-data && mkdir postgres-data && sudo chown 999:999 postgres-data && docker compose up
```

Better:
```
rm -rf ./minetest-data/

if [ ! -d minetest-data/games/mineclonia/ ] ; then
    mkdir -p minetest-data/games/
    wget -O mineclonia.zip https://content.luanti.org/packages/ryvnf/mineclonia/releases/28965/download/
    unzip mineclonia.zip -d minetest-data/games/
    rm mineclonia.zip
fi

sudo rm -rf postgres-data/
mkdir postgres-data/
sudo chown $USER:999 postgres-data/

docker compose up
```

Setting up the server info:

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