#!/bin/bash

if [ -e server.pid ]
then
  kill `cat server.pid`
  rm server.pid
fi