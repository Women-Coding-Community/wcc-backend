FROM eclipse-temurin:21

ARG JAR_FILE=build/libs/*.jar

WORKDIR /app

ENV JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"

COPY ${JAR_FILE} /app/app.jar
COPY src/main/resources /app/resources

EXPOSE 8080 5005

CMD ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]