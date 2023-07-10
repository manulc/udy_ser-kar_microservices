FROM eclipse-temurin:17-jre-alpine
COPY target/photoAppApiConfigServer-0.0.1-SNAPSHOT.jar ConfigServer.jar
EXPOSE 8012
ENTRYPOINT ["java","-jar","ConfigServer.jar"]