#!/bin/bash

#The port on which the TLB server is started.
export TLB_PORT=7019

#This is the number of days for which a given version's data is kept. -1 means it will never be purged
export VERSION_LIFE_IN_DAYS=7

#This is used as 'alpha' in the Smoothened balancing 
export SMOOTHING_FACTOR=1

#The store is the directory under which the test information is stored
export tlb_store=tlb_store

nohup java -jar tlb-all*.jar &
echo $! > server.pid