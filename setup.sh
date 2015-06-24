#!/bin/bash

set -eou pipefail
IFS=$'\n\t'

SPIGOT_URL='https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar'

mkdir -p server/srvbuild/

wget $SPIGOT_URL -O server/srvbuild/BuildTools.jar
pushd ./
cd server/srvbuild
java -jar BuildTools.jar
popd
