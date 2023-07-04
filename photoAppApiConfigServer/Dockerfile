FROM openjdk:11-jre-slim-buster
COPY target/photoAppApiConfigServer-0.0.1-SNAPSHOT.jar ConfigServer.jar
EXPOSE 8012
ENTRYPOINT ["java","-jar","ConfigServer.jar"]