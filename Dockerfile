FROM fintlabsacr.azurecr.io/kunde-selvregistrering-frontend:latest as client

FROM gradle:4.10.3-jdk8-alpine as builder
USER root
COPY . .
COPY --from=client /src/build/ src/main/resources/static/
RUN gradle --no-daemon build

FROM gcr.io/distroless/java:8
#COPY --from=builder /home/gradle/build/deps/external/*.jar /data/
#COPY --from=builder /home/gradle/build/deps/fint/*.jar /data/
COPY --from=builder /home/gradle/build/libs/fint-kunde-selvregistrering-backend-*.jar /data/app.jar
CMD ["java", "-jar", "/data/app.jar"]
