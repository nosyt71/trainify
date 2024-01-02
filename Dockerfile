FROM maven:3-eclipse-temurin-21 AS builder

#working and target dir
WORKDIR /src

#copy all the required stuff
COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .
COPY .mvn .mvn
COPY src src

#build app
RUN mvn package -Dmaven.test.skip=true


FROM maven:3-eclipse-temurin-21
WORKDIR /app

# copy and rename to app.jar
COPY --from=builder /src/target/demo-0.0.1-SNAPSHOT.jar app.jar

ENV PORT=8080
ENV SPRING_REDIS_HOST=localhost SPRING_REDIS_PORT=1234
ENV SPRING_REDIS_DATABASE=0
ENV SPRING_REDIS_USERNAME=default SPRING_REDIS_PASSWORD=abc123

EXPOSE $PORT

ENTRYPOINT SERVER_PORT=${PORT} java -jar ./app.jar