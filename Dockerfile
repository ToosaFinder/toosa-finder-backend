FROM openjdk:11-jre-slim
WORKDIR /toosafinder
ARG JAR_FILE=server/build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]