FROM openjdk:11-jre-slim

WORKDIR /app

COPY target/scala-2.13/Univer-assembly-0.0.1.jar ./

CMD ["java", "-jar", "Univer-assembly-0.0.1.jar"]
