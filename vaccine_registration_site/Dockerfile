FROM openjdk:11-jre-slim
ENV spring_profiles_active=docker
COPY /target/*-0.0.1-SNAPSHOT.jar /app.jar
CMD ["java","-jar","/app.jar"]
