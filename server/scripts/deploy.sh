#!/usr/bin/env sh

function build_client {
  pushd .
  cd ../client
  lein cljsbuild clean
  lein cljsbuild once development
  ssh dupe@192.241.196.82 'rm -rf public'
  scp -r public dupe@192.241.196.82:
  popd
}

function build_server {
  rm target/*.jar
  lein uberjar
  scp target/server-standalone.jar dupe@192.241.196.82:server.jar
  ssh dupe@192.241.196.82 'sudo supervisorctl restart dupe'
}

if [ "$1" = "client" ]; then
  build_client
elif [ "$1" = "server" ]; then
  build_server
else
  build_client
  build_server
fi
