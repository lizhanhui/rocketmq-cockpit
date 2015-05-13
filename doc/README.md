# Deployment Guide

### Quick Note 
Full features mentioned here requires RocketMQ [fork](https://github.com/lizhanhui/alibaba_rocketmq). If you choose to 
use official [release](https://github.com/alibaba/rocketmq), the following features will not be available:

1. SSL Transmission
2. Message Tracing
3. Flow Control


### Prerequisite
* MySQL 5.5+
* Java 1.7.x
* RocketMQ 3.2.2+
* Jetty 9.x
* Maven 3.x


### Deployment Steps

1. Setup Database

       * Execute doc/sql/create-table.sql to create cockpit database;

       * Execute doc/sql/setup.sql to populate required data.
   
2. Compile, Package

       * Assume jetty is located at ${HOME}/jetty;

       * At rocketmq-cockpit folder, execute `bash deploy.sh`
   
3. Start Jetty
       * Go to jetty HOME directory and start jetty, you may need administrator role if your jetty port is under 1024.
   
         `cd ~/jetty/ && bash start.sh`

