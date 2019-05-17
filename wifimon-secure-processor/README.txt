# docker instructions
mvn package docker:build
docker push wifimon/processor
docker run -p 8443:8443 -t wifimon/secure-processor
