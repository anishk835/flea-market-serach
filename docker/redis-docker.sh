#!/bin/bash

# Check if REDIS_PASSWORD environment variable is set
if [ -z "$REDIS_PASSWORD" ]; then
    echo "Error: REDIS_PASSWORD environment variable is not set"
    echo "Please set it with: export REDIS_PASSWORD=your-secure-password"
    exit 1
fi

mkdir -p {redis-0}

CONFIG="protected-mode yes
port 6379
#authentication
masterauth $REDIS_PASSWORD
requirepass $REDIS_PASSWORD"

echo "$CONFIG" > "${PWD}"/redis-0/redis.conf

docker run -d -p 6379:6379 --rm --name redis-0 \
    --net redis -v ${PWD}/redis-0:/etc/redis/ \
    redis:6.0-alpine redis-server /etc/redis/redis.conf
