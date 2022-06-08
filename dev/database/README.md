# Database

This directory contains example setup for development purposes.

Running application locally requires real database backend and one
can provide one with minimal hassle by utilizing docker.

`docker-compose up -d`

## Seeding the test data

Database needs to have some pre-definend roles which are initialised by
[docker-entrypoint-initdb.d/01_create_oph_role.sql](docker-entrypoint-initdb.d/01_create_oph_role.sql).

If one needs to seed database with some predefined dataset, just copy the
needed (plain) **sql** into [docker-entrypoint-initdb.d](docker-entrypoint-initdb.d) directory. Files will be
executed in lexical order during container initialization.

## Recreate database

If one wants to recreate container for example for seeding with different data set
all volumes need to be dropped also.

`docker-compose down --volumes`
