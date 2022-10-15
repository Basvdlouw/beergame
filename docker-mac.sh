#!/usr/bin/env bash

CONTAINER=beergame
COMMAND=/bin/bash
NIC=en0

# Grab the ip address of this box
IPADDR=$(ifconfig $NIC | grep inet | awk '$1=="inet" {print $2}')

xhost + ${IPADDR}

DISP_NUM=$(jot -r 1 100 200)  # random display number between 100 and 200
PORT_NUM=$((6000 + DISP_NUM)) # so multiple instances of the container won't interfer with eachother

socat TCP-LISTEN:${PORT_NUM},reuseaddr,fork UNIX-CLIENT:\"$DISPLAY\" 2>&1 > /dev/null &

DISPLAY=$IPADDR:$DISP_NUM docker-compose up

rm -f $XAUTH
kill %1       # kill the socat job launched above