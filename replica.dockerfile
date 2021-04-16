FROM openjdk:11
COPY config config
COPY out/artifacts/CSDTP1_jar/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]