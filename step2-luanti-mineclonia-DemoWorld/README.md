# Mineclonia in Docker - demo

Run it:
```shell
docker run -it --rm -p 30000:30000/udp -e "CLI_ARGS=--gameid mineclonia" --name luanti-mineclonia-demo ondrejzizka/luanti-mineclonia-demo
```

See it:
```shell
docker exec -it luanti-mineclonia-demo sh
# ls -la config/.minetest/games/
```

##  Kill it:
```shell
docker container kill luanti-mineclonia-demo
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

