# TimeTracker
A simple web application that works with legacy TimeTracker service

## How to build
In order to build and create docker image from the source code, you can simply run the following command:
```
mvn clean package
```  
This will download all dependencies and compile the application. Then, it will create docker image and tag it with the artifact version number.

## Deploying with Docker
Since the docker image has to connect with legacy service (which is also a dockerized application), before starting any containers, you should create a docker network first:
```
docker network create timetracker-net
```
This will create a network with the default driver as "bridge".

Then, run the following command to start legacy service and connect it to the network:
```
docker run --name legacyservice --network timetracker-net -d -p 8085:8080 alirizasaral/timetracker:1
```
Finally, run the following command to start timetracker-web application and connect it to the network:
```
docker run --name timetracker-web --network timetracker-net -d -e "LEGACY_SERVICE_URL=http://legacyservice:8080" -p 8080:8080 timetracker/timetracker-bff:0.0.1-SNAPSHOT
```
You can test the application by opening http://localhost:8080 on your browser.
