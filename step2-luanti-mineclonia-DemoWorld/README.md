# Mineclonia in Docker - demo

## Usage
https://github.com/luanti-org/luanti/blob/master/minetest.conf.example

To run it:
```shell
docker run -it --rm -p 30000:30000/udp -e "CLI_ARGS=--gameid mineclonia" --name luanti-mineclonia-demo ondrejzizka/luanti-mineclonia-demo
```

To see it:
```shell
docker exec -it luanti-mineclonia-demo sh
# ls -la config/.minetest/games/
```

###  To terminate it:
```shell
docker container kill luanti-mineclonia-demo
```



## Setting up the server info

Here is a [Luanti config template](https://github.com/luanti-org/luanti/blob/master/minetest.conf.example).

Here is the official docs page for hosting a Luanti server: https://docs.luanti.org/for-server-hosts/

A script to replace some values:  (First, delete if they exist, then add.) 

```bash
CONFIG_FILE=./minetest-data/main-config/minetest.conf

sed -i '/^server_announce/d' "$CONFIG_FILE"
sed -i '/^server_name/d' "$CONFIG_FILE"
sed -i '/^server_description/d' "$CONFIG_FILE"
sed -i '/^default_game/d' "$CONFIG_FILE"

echo "server_announce = true" >> "$CONFIG_FILE"
echo "server_name = My Mineclonia Server" >> "$CONFIG_FILE"
echo "server_description = A Luanti / Mineclonia server" >> "$CONFIG_FILE"
echo "default_game = mineclonia" >> "$CONFIG_FILE"
```


### To create a world of Mineclonia game by launching it:

```shell
timeout 5 \
docker run -d --name=luanti-world-creator \
  -e PUID=1000 \
  -e PGID=1000 \
  -e TZ=Etc/UTC \
  -e CLI_ARGS="--gameid mineclonia" \
  -p 30000:30000/udp \
  -v ./created-worlds/:/config/.minetest/worlds/ \
  --restart unless-stopped \
  ondrejzizka/luanti-mineclonia-demo:latest
```

