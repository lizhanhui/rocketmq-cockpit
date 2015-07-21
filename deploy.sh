#!/bin/sh
mvn package
cp cockpit-web/target/cockpit-web-3.2.2.R1.war ~/jetty/webapps/
cp doc/cockpit-web-3.2.2.R1.xml ~/jetty/webapps/
cp doc/start.sh ~/jetty/