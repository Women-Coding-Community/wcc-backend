#!/bin/bash -xe

# Script that kills the test environment and cleanup volumes

dir="$(dirname -- "$(which -- "$0" 2>/dev/null || realpath -- "./$0")")"
docker compose --ansi never \
  --file "$dir/../docker/docker-compose.yml" \
  kill
if [ "$(docker ps -a -q)" ]; then
  docker rm -f $(docker ps -a -q)
fi
if [ "$(docker volume ls -q)" ]; then
  docker volume rm $(docker volume ls -q)
fi
