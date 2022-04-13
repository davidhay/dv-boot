#!/bin/bash

java -version

#this script runs the datavault webapp in 'standalone' mode - this helps test Spring and SpringSecurity config.
#this script uses executable WAR file.
SPRING_PROFILES_ACTIVE=standalone java -jar datavault-webapp/target/datavault-webapp.war
