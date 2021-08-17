FROM openjdk:11.0.11-jdk-oracle
ARG JAR_FILE=build/libs/*-all.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8081
ENV APP_NAME keymanager-rest
ENTRYPOINT ["java", "-jar", "/app.jar"]