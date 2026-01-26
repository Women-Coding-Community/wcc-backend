#!/bin/bash

# Script that creates the test environment (bringuing it down before) by
# building the springboot-app (--build), and
# starting in detached mode (-d), and waiting for it to be healthy (--wait) 
#
# Also checks how much time the application needed to be healthy in the end

# Bring it down
dir="$(dirname -- "$(which -- "$0" 2>/dev/null || realpath -- "./$0")")"
$dir/docker-down.sh

# Put it up with --build and --wait
docker compose --ansi never \
  --file "$dir/../docker/docker-compose.yml" \
  up --build -d --wait

# Checks how long it took, from "Now" in the container to the StartedAt time 
# we don't use the local clock to avoid issues with different timezones
APP=springboot-app
NOW_TIME=$(docker exec $APP date "+%H:%M:%S")
NOW_SECONDS=$(date -d "$NOW_TIME" +%s 2>/dev/null || date -j -f "%H:%M:%S" "${NOW_TIME%.*}" +%s)
echo "Now on $APP is: $NOW_TIME ($NOW_SECONDS)"
START_TIME=$(docker inspect --format='{{.State.StartedAt}}' $APP)
START_SECONDS=$(date -d "$START_TIME" +%s 2>/dev/null || date -j -f "%Y-%m-%dT%H:%M:%S" "${START_TIME%.*}" +%s)
echo "Started $APP at: $START_TIME ($START_SECONDS)"
echo "Time to be healthy (in seconds): $((NOW_SECONDS - START_SECONDS))"