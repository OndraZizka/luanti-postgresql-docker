

### To build and push:

```shell
docker build -f ./Dockerfile -t ondrejzizka/luanti-mineclonia:latest .
docker login -u ondrejzizka   ## Needs a password from a personal token from Dockerhub.
docker push ondrejzizka/luanti-mineclonia:latest
````

Run it:
```shell
DOCKERHUB_USER=ondrejzizka
docker run -it --rm -e "CLI_ARGS=--gameid mineclonia" --name luanti-mineclonia-bare $DOCKERHUB_USER/luanti-mineclonia
```

See it:
```shell
docker exec -it luanti-mineclonia-bare sh
# ls -la config/.minetest/games/
```

##  Kill it:
```shell
docker container kill luanti-mineclonia-bare
```



### Structure of the Luanti dir

```shell
~/uw/luanti$ tree -d -L 3 /home/o/snap/minetest/
/home/o/snap/minetest/
├── 1934
│   ├── client
│   │   └── serverlist
│   ├── games
│   │   └── mineclonia
│   ├── mod_data
│   ├── mods
│   │   ├── animalia
│   │   ├── animalworld
│   │   ├── bonemeal
│   │   ├── creatura
│   │   ├── dfcaverns
│   │   ├── everness
│   │   ├── handle_schematics
│   │   ├── illumination
│   │   ├── leads
│   │   ├── mapgen_rivers
│   │   ├── mg_villages
│   │   ├── mobs
│   │   ├── mobs_animal
│   │   ├── moreblocks
│   │   ├── moretrees
│   │   ├── protector
│   │   ├── simple_woodcutter
│   │   └── xcompat
│   └── worlds
│       └── testsveta
├── common
└── current -> 1934
```

