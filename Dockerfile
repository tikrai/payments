FROM openjdk:8-jdk-slim
ENTRYPOINT ["java", "-jar", "/usr/share/tikrai/payments.jar"]
ARG JAR_FILE
ADD target/${JAR_FILE} /usr/share/tikrai/payments.jar