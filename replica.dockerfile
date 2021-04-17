FROM openjdk:11
COPY config config
COPY replica/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]