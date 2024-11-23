FROM openjdk:21-jdk
WORKDIR /app
COPY gradle /app/gradle
COPY gradlew /app/
COPY build.gradle /app/
RUN chmod +x gradlew
COPY . .
RUN microdnf update -y && microdnf install -y findutils
RUN ./gradlew build -x test
CMD ["./gradlew", "bootRun"]
