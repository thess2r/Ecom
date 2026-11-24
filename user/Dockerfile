FROM eclipse-temurin:17-jre-jammy

WORKDIR /app
COPY user/target/user-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
