FROM openjdk:21

WORKDIR /app

COPY build/libs/notificacao-0.0.1-SNAPSHOT.jar /app/notificacao.jar

EXPOSE 8082

CMD ["java", "-jar", "/app/notificacao.jar"]