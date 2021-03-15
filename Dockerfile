FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD target/files-converter-API.jar filesconverterapidocker.jar
ENTRYPOINT ["java", "-jar","filesconverterapidocker.jar"]
EXPOSE 8080