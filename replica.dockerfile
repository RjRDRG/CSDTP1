FROM openjdk:11
COPY config_in_docker config
COPY replica/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]