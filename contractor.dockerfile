FROM openjdk:11
COPY config_in_docker config
COPY keystore keystore
COPY contractor.policy contractor.policy
COPY security.conf security.conf
COPY contractor/target/*.jar app.jar