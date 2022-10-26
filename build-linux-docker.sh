#!/bin/sh

docker pull ghcr.io/softinstigate/graalvm-maven-docker
docker run -it --rm \
    -v "$PWD":/opt/app  \
    -v "$HOME"/.m2:/root/.m2 \
    ghcr.io/softinstigate/graalvm-maven-docker \
    -Pnative -DskipTests clean package
