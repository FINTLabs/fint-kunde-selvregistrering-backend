FROM gradle:7.3 as builder
USER root
COPY . .
RUN gradle --no-daemon build

FROM gcr.io/distroless/java11
ENV JAVA_TOOL_OPTIONS -XX:+ExitOnOutOfMemoryError
COPY --from=builder /home/gradle/build/libs/fint-kunde-selvregistrering-backend*.jar /data/app.jar
CMD ["/data/app.jar"]
