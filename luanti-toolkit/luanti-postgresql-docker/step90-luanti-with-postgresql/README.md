




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


## Running Luanti with Mineclonia backed by PostgreSQL
After all above is done (currently not automated), run:

```
docker compose up
```

PostgreSQL should start, Luanti should connect to it, and start saving blocks to the DB.