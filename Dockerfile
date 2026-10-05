FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
COPY test ./test
RUN mvn -B package -DskipTests

FROM eclipse-temurin:21-jre-noble
RUN apt-get update \
    && apt-get install -y --no-install-recommends xvfb x11vnc novnc websockify openbox procps \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /build/target/sistema-contable-1.0.0.jar /app/sistema-contable.jar
COPY docker/app/entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh
EXPOSE 6080
ENTRYPOINT ["/app/entrypoint.sh"]
