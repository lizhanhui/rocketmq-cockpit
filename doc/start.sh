#!/bin/sh
screen java -server -XX:-UseConcMarkSweepGC -XX:-UseParallelGC -Xms256m -Xmx256m -Denable_ssl=true  -jar start.jar
