FROM postgres:17

#RUN useradd postgres  ## Already exists

#RUN chmod u+rwx /var/lib/postgresql/data
RUN chown -R postgres /var/lib/postgresql/data

USER postgres
