#!/bin/bash

mvn clean deploy -DskipTests -P rdc --settings ~/.m2/settings-common.xml
