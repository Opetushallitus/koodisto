#!/bin/sh
#mvn jetty:run -Pui-development -Ddaemon=false
mvn tomcat:run -Pservlet-development,ui-development
