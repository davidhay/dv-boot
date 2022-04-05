#!/bin/bash

java -version

#this script runs the datavault webapp in 'standalone' mode - this helps test Spring and SpringSecurity config.
SPRING_PROFILES_ACTIVE=standalone ./mvnw spring-boot:run --projects datavault-webapp