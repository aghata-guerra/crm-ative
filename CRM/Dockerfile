
# Etapa 1: compilar o Spring Boot
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY . .

RUN chmod +x mvnw

RUN ./mvnw clean package -DskipTests

# Etapa 2: executar a aplicação
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 10000

CMD ["sh", "-c", "exec java -Dserver.port=${PORT:-10000} -jar app.jar"]
