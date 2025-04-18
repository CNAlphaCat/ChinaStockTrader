FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
COPY --from=build /app/chinastocktrader/target/chinastocktrader-0.0.1.jar /app/app.jar
EXPOSE 8147
CMD ["java", "-jar", "/app/app.jar"]