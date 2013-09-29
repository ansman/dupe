pushd .
cd ../client
lein cljsbuild once development
ssh dupe@192.241.196.82 'rm -rf public'
scp -r public dupe@192.241.196.82:

popd
rm target/*.jar
lein uberjar
scp target/server-standalone.jar dupe@192.241.196.82:server.jar
ssh dupe@192.241.196.82 'sudo supervisorctl restart dupe'
