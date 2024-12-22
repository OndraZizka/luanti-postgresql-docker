FROM postgres:17

#RUN useradd postgres  ## Already exists
#USER postgres  ## Already so in the parent image.

#RUN chmod u+rwx /var/lib/postgresql/data
#RUN chown -R postgres /var/lib/postgresql/data

## PostgreSQL image runs under UID 999, and a directly mounted (bind mounted) volume needs to be writable by that user.
RUN echo "chown -R postgres:postgres /var/lib/postgresql/data" >> /docker-entrypoint-initdb.d/changeDataDirOwner.sh
RUN chmod +x /docker-entrypoint-initdb.d/changeDataDirOwner.sh
