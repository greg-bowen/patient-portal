FROM eclipse-temurin:21-jdk AS builder
RUN mkdir /app
RUN apt-get update && apt-get install -y maven
ADD .  /app
WORKDIR  /app
RUN java -version
RUN mvn -version
RUN mvn -f /app/pom.xml clean package
FROM eclipse-temurin:21-jre
COPY --from=builder /app/target/patient-portal-0.0.1-SNAPSHOT.jar   app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
