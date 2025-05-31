## Postgres

<!-- TOC -->

* [Postgres](#postgres)
    * [Fly.io setup](#flyio-setup)
    * [Destroy setup](#destroy-setup)
    * [Managing](#managing)
        * [Start proxy to connect to the database locally:](#start-proxy-to-connect-to-the-database-locally)
        * [Connect with fly.io postgres CLI:](#connect-with-flyio-postgres-cli)
        * [Create database and table](#create-database-and-table)

<!-- TOC -->

### Fly.io setup

```shell
flyctl auth login
flyctl postgres create
flyctl postgres connect -a wcc-postgres
flyctl proxy 5432 -a wcc-postgres # Connect to the database locally
flyctl postgres connect -a wcc-postgres  # Connect to the database via psql
```

### Destroy setup

```shell
flyctl postgres detach --app wcc-backend wcc-postgres
flyctl volumes list -a wcc-postgres
flyctl volumes destroy <volume-id> -a wcc-postgres
flyctl apps destroy wcc-postgres
flyctl apps list # Should not show wcc-postgres
flyctl volumes list # No leftover volumes
```

### Managing

#### Start proxy to connect to the database locally:

```shell 
flyctl proxy 5432 -a wcc-postgres
```

#### Connect with fly.io postgres CLI:

```shell
flyctl postgres connect -a wcc-postgres
```

#### Create database and table

```shell
CREATE database wcc;
\c wcc
CREATE TABLE IF NOT EXISTS page(id TEXT PRIMARY KEY, data JSONB NOT NULL);
```

#### Logs

```shell
fly logs -a wcc-postgres # View logs for the postgres app
```