# docker instructions
mvn package docker:build
docker push wifimon/processor
docker run -p 9000:9000 -t wifimon/processor
