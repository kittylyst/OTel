FROM registry.access.redhat.com/ubi8/openjdk-17:1.13-1
USER root
RUN mkdir /app
COPY animals-demo-1.0-SNAPSHOT-jar-with-dependencies.jar  /app
WORKDIR /app
CMD ["java", "-jar" , "animals-demo-1.0-SNAPSHOT-jar-with-dependencies.jar"]

