FROM docker.io/library/eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /src/order-service

COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle ./gradle

RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

COPY src ./src

RUN ./gradlew bootJar --no-daemon

FROM docker.io/library/eclipse-temurin:21-jre-alpine AS runner

ARG USER_NAME=order-service
ARG USER_UID=1000
ARG USER_GID=${USER_UID}

RUN addgroup -g ${USER_GID} ${USER_NAME} \
    && adduser -h /opt/be-profile -D -u ${USER_UID} -G ${USER_NAME} ${USER_NAME}

USER ${USER_NAME}
WORKDIR /opt/order-service
COPY --from=builder --chown=${USER_UID}:${USER_GID} /src/order-service/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java"]
CMD ["-jar", "app.jar"]