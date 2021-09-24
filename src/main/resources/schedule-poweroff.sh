#!/bin/bash

PID=$1

while true
do
  if ps -p $PID > /dev/null
  then
      sleep 5
  else
      sudo poweroff
      exit
  fi
done
