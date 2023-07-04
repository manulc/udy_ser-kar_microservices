FROM openjdk:11-jre-slim-buster
COPY target/photoAppZuulApiGateway-0.0.1-SNAPSHOT.jar ZuulApiGateway.jar
EXPOSE 8011
ENTRYPOINT ["java","-jar","ZuulApiGateway.jar"]