#!/bin/sh

this_folder="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
parent_folder=$(dirname $this_folder)

NAME=solver-api
IMG=$NAME
IMG_VERSION=0.0.1-SNAPSHOT
DOCKER_HUB_REPOSITORY=caquicode
DOCKER_HUB_IMG=$DOCKER_HUB_REPOSITORY/$IMG:$IMG_VERSION
CONTAINER=$NAME
PORT=7705
DEBUG_PORT=7605


echo "going to run container from image $DOCKER_HUB_IMG"
docker rm $CONTAINER
docker run -d --name $CONTAINER -p $PORT:$PORT -p $DEBUG_PORT:$DEBUG_PORT -e DEBUG_PORT=$DEBUG_PORT -e PORT=$PORT $DOCKER_HUB_IMG

echo "... done."