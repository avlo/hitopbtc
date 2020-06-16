FROM openjdk:latest
VOLUME /tmp
EXPOSE 80
COPY target/hitop-0.1.0.jar hitop.jar
ENTRYPOINT ["java","-jar","/hitop.jar"]
