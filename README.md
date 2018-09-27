persistence-api microservice stores data in mongo database.

## Configuration:
Expected configuration file located at /config/application.properties.  This file must have some
or all of the following properties specified

# Place application.properties in mountable location and start the instance similar to below.

docker run --network=host --rm --name persistence -it -p 8080:8080 -v /data/persistence-api/configuration/:/config/ peristence-api:latest

# Service config
server.port=8080

#Logging configuration
logging.level.root=INFO
logging.level.cms.sre=INFO