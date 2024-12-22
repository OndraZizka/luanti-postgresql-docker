
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