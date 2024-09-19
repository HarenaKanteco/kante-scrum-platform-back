#
# Build stage
#
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -Pprod -DskipTests

#
# Package stage
#
FROM openjdk:17-jdk-slim
COPY --from=build /target/kante-0.0.1-SNAPSHOT.jar kante.jar
# ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java","-jar","kante.jar"]