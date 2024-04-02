FROM fintlabsacr.azurecr.io/kunde-selvregistrering-frontend:latest as client

FROM gradle:6.7.1-jdk11 as builder
USER root
COPY . .
COPY --from=client /src/build/ src/main/resources/static/
RUN gradle --no-daemon build

FROM gcr.io/distroless/java11-debian11
#COPY --from=builder /home/gradle/build/deps/external/*.jar /data/
#COPY --from=builder /home/gradle/build/deps/fint/*.jar /data/
COPY --from=builder /home/gradle/build/libs/fint-kunde-selvregistrering-backend-*.jar /data/app.jar
CMD ["/data/app.jar"]
