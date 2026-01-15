
FROM eclipse-temurin:11-jdk
ARG PROJECT_VERSION=0.1.0
RUN mkdir -p /home/app
WORKDIR /home/app
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
ENV SPRING_PROFILES_ACTIVE docker
COPY target/service-discovery-v${PROJECT_VERSION}.jar service-discovery.jar
EXPOSE 8761
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "service-discovery.jar"]


