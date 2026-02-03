FROM openjdk:17-slim-buster
ENV TZ=Asia/Shanghai
COPY oil-template-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]