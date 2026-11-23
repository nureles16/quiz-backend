# Use an official JDK image as the base
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app
VOLUME /tmp

# Copy the Maven build jar to the image
COPY target/quiz-app-0.0.1-SNAPSHOT.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

#version: '3.8'
#
#services:
#  db:
#    image: postgres:16
#    container_name: quiz-db
#    environment:
#       POSTGRES_DB: quiz_app
#       POSTGRES_USER: postgres
#       POSTGRES_PASSWORD: admin
#    ports:
#       - "5432:5432"
#    networks:
#       - quiz-net
#    healthcheck:
#       test: ["CMD-SHELL", "pg_isready -U postgres"]
#       interval: 5s
#       timeout: 5s
#       retries: 5
#
#  backend:
#    image: almazbekuulunureles/quiz-backend:latest
#    container_name: quiz-backend
#    ports:
#      - "8080:8080"
#    depends_on:
#       db:
#        condition: service_healthy
#    networks:
#      - quiz-net
#    environment:
#      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/quiz_app
#      SPRING_DATASOURCE_USERNAME: postgres
#      SPRING_DATASOURCE_PASSWORD: admin
#    restart: always
#
#  frontend:
#    image: almazbekuulunureles/quiz-frontend:latest
#    container_name: quiz-frontend
#    ports:
#      - "80:80"
#    depends_on:
#      - backend
#    networks:
#      - quiz-net
#
#networks:
#  quiz-net: