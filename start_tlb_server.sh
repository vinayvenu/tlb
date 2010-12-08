#!/bin/bash

#The port the TLB server would listen to
#export TLB_PORT=7019 #uncomment to override, 7019 is the default value

#Number of days a given version of data survives(before being considered garbage by TLB and deleted). -1 means it will never be purged.
export VERSION_LIFE_IN_DAYS=7

#This is used as 'alpha' in the Smoothened balancing (refer. http://en.wikipedia.org/wiki/Exponential_smoothing for details)
export SMOOTHING_FACTOR=0.5 # 0.5 means half weightage to last run time, other half to historical run time

#Store is the directory under which the test information(running-time, results etc) are stored
#export tlb_store=tlb_store #uncomment to override, tlb_store is the default value

#find latest tlb jar
tlb_jar=`ls -t | grep '^tlb-all.*\.jar$'`

nohup java -jar $tlb_jar &
server_pid=$!
echo $server_pid > .server.pid
echo "Server started, PID: $server_pid"