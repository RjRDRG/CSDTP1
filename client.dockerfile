FROM openjdk:11
COPY keystore keystore
COPY security.conf security.conf
COPY client/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]