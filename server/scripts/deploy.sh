#!/usr/bin/env sh

function deploy_client {
  pushd .
  cd ../client
  lein cljsdeploy clean
  lein cljsdeploy once development
  ssh dupe@192.241.196.82 'rm -rf public'
  scp -r public dupe@192.241.196.82:
  popd
}

function deploy_server {
  rm target/*.jar
  lein uberjar
  scp target/server-standalone.jar dupe@192.241.196.82:server.jar
  ssh dupe@192.241.196.82 'sudo supervisorctl restart dupe'
}

if [ "$1" = "client" ]; then
  deploy_client
elif [ "$1" = "server" ]; then
  deploy_server
else
  deploy_client
  deploy_server
fi
