FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD target/files-converter-API.jar filesconverterapi-dockerFile.jar
ENTRYPOINT ["java", "-jar","filesconverterapi-dockerFile.jar"]
EXPOSE 8080