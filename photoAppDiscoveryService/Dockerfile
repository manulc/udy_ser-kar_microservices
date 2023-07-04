FROM openjdk:11-jre-slim-buster
COPY target/photoAppDiscoveryService-0.0.1-SNAPSHOT.jar DiscoveryService.jar
EXPOSE 8010
ENTRYPOINT ["java","-jar","DiscoveryService.jar"]