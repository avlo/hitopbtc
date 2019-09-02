FROM openjdk:latest
VOLUME /tmp
EXPOSE 80
COPY build/libs/hitop-0.1.1.jar hitop.jar
ENTRYPOINT ["java","-jar","/hitop.jar"]
