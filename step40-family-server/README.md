

# Our family's server setup

This applies our custom config values and our favorite settings.


To build and push:

```shell
docker build -f ./Dockerfile -t ondrejzizka/luanti-mineclonia-zizka:latest .
docker push ondrejzizka/luanti-mineclonia-zizka:latest
```

Tag a dated version:
```shell
now=$(printf '%(%Y-%m-%d_%H%M)T\n' -1)
docker tag ondrejzizka/luanti-mineclonia-zizka:latest ondrejzizka/luanti-mineclonia-zizka:v$now
docker push ondrejzizka/luanti-mineclonia-zizka:v$now
```

Run it:
```shell
docker run -it --rm -e "CLI_ARGS=--gameid mineclonia" --name luanti-mineclonia-zizka ondrejzizka/luanti-mineclonia-zizka
```

Shell within the container:
```shell
docker exec -it luanti-mineclonia-mods sh
# ls -la config/.minetest/games/
```

##  Kill it:
```shell
docker container kill luanti-mineclonia-zizka
```
