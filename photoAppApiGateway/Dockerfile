FROM eclipse-temurin:17-jre-alpine
COPY target/photoAppApiGateway-0.0.1-SNAPSHOT.jar ApiGateway.jar
EXPOSE 8011
ENTRYPOINT ["java","-jar","ApiGateway.jar"]