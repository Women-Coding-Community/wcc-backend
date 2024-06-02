FROM eclipse-temurin:21

COPY build/libs/wcc-platform.jar app.jar

# Set the entry point to run your application
ENTRYPOINT ["java","-jar","/app.jar"]