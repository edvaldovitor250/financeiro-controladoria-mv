FROM eclipse-temurin:8-jre
WORKDIR /app
COPY target/financeiro-controladoria-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
