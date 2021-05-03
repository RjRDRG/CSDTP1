FROM openjdk:11
COPY proxy/src/main/resources/keystore src/main/resources/keystore
COPY config_in_docker config
COPY proxy/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]