#!/bin/bash

# Run the nginx in a podman container
podman run -t \
       --rm \
       --name website \
       -p 8083:8080 \
       -p 8084:8081 \
       -v $PWD/nginx.conf:/etc/nginx/nginx.conf \
       -v $PWD/docs/target/site:/var/www/html/docs \
       -v $PWD/site/target:/var/www/html/site \
       -v $PWD/assets:/var/www/html/assets \
       docker.io/nginx:latest
