#!/bin/bash

# Run the nginx in a podman container
podman run -it \
       -p 8083:8080 \
       -v $PWD/nginx.conf:/etc/nginx/nginx.conf \
       -v $PWD/docs/target/site:/var/www/html/docs \
       -v $PWD/site/target:/var/www/html/site \
       docker.io/nginx:latest
