FROM postgres:17

## PostgreSQL image runs under user postgres, UID 999, group postgres, GID 999.
## The directly mounted (bind mounted) volumes need to be writable by that user.

RUN echo "chown -R postgres:postgres /var/lib/postgresql/data" >> /docker-entrypoint-initdb.d/changeDataDirOwner.sh
RUN chmod +x /docker-entrypoint-initdb.d/changeDataDirOwner.sh
