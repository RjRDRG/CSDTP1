FROM openjdk:11
COPY proxy/src/main/resources/keystore src/main/resources/keystore
COPY config config
COPY out/artifacts/proxy_jar/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]