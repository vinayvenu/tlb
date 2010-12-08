#!/bin/bash

pid_file=.server.pid

if [ -e $pid_file ]; then
    kill `cat $pid_file`
    rm $pid_file
else 
    echo "Couldn't find the server's process id, pid file($pid_file) doesn't exist."
    exit 1
fi