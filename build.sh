#!/bin/bash

./gradlew clean build --no-build-cache -xtest
scp -r dingo-dist/dingo/libs/ root@172.20.3.30:/opt/program/dingo/
scp -r dingo-dist/dingo/libs/ root@172.20.3.31:/opt/program/dingo/
scp -r dingo-dist/dingo/libs/ root@172.20.3.32:/opt/program/dingo/
