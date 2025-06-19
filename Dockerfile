FROM openjdk:11
VOLUME /tmp
EXPOSE 8080
ADD ./target/ms-client-0.0.1-SNAPSHOT.jar ms-client.jar
ENTRYPOINT ["java","-jar","/ms-client.jar"]