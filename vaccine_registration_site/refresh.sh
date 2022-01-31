#!/bin/bash
docker container prune
mvn clean compile package
docker-compose build && docker-compose up
