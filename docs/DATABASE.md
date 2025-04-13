## Postgres

<!-- TOC -->

* [Postgres](#postgres)
    * [Fly.io setup](#flyio-setup)
    * [Destroy setup](#destroy-setup)
* [Managing](#managing)

<!-- TOC -->

### Fly.io setup

flyctl postgres connect -a wcc-postgres

### Destroy setup

```
flyctl postgres detach --app wcc-backend wcc-postgres
flyctl volumes list -a wcc-postgres
flyctl volumes destroy <volume-id> -a wcc-postgres

flyctl apps destroy wcc-postgres

flyctl apps list          # Should not show wcc-postgres
flyctl volumes list       # No leftover volumes
```

### Managing

Start proxy to connect to the database locally:

```shell 
flyctl proxy 5432 -a wcc-postgres
```

Access directly fly.io database:

```shell
flyctl postgres connect -a wcc-postgres