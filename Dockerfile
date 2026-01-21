FROM gradle:9-jdk25-alpine AS build

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon -x test

FROM amazoncorretto:25-jdk

COPY --from=build /home/gradle/src/build/libs/*.jar /app/app.jar
WORKDIR /app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

