FROM openjdk:11
COPY config_in_docker config
COPY keystore keystore
COPY security.conf security.conf
COPY proxy/target/*.jar app.jar