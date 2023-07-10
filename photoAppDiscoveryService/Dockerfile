FROM eclipse-temurin:17-jre-alpine
COPY target/photoAppDiscoveryService-0.0.1-SNAPSHOT.jar DiscoveryService.jar
EXPOSE 8010
ENTRYPOINT ["java","-jar","DiscoveryService.jar"]