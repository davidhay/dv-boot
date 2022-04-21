#!/bin/bash

java -version

#this script runs the datavault webapp in 'standalone' mode - this helps test Spring and SpringSecurity config.
#this script uses executable WAR file.
#to ensure that the war file exists!
./mvnw -Dmaven.test.skip=true package
DATAVAULT_HOME=$PWD SPRING_PROFILES_ACTIVE=standalone java -jar datavault-webapp/target/datavault-webapp.war
