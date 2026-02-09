# build stage
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml ./
COPY src ./src
RUN mvn clean package -DskipTests

# end stage
FROM amazoncorretto:21.0.2-alpine3.19
WORKDIR /app
COPY --from=build /app/target/API.jar app.jar
RUN ln -sf /usr/share/zoneinfo/America/Sao_Paulo /etc/localtime
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]