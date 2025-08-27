# Dockerfile
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copia o pom.xml e baixa dependências (cache eficiente)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código fonte e compila
COPY src ./src
RUN mvn package -DskipTests -B

# Imagem final mais leve (apenas JRE)
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copia o JAR da etapa de build
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]