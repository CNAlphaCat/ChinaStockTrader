FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
COPY pom.xml /app/
COPY chinastockdata/pom.xml /app/chinastockdata/
COPY chinastocktrader/pom.xml /app/chinastocktrader/
WORKDIR /app
RUN mvn -B dependency:go-offline

COPY . /app
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
COPY --from=build /app/chinastocktrader/target/chinastocktrader-0.0.1.jar /app/app.jar
EXPOSE 8147
CMD ["java", "-jar", "/app/app.jar"]