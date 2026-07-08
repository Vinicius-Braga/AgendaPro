# Estágio 1: build — compila o jar usando o Gradle wrapper do projeto.
FROM gradle:8.7-jdk21 AS build
WORKDIR /app

# Copia só o necessário para resolver as dependências primeiro. Enquanto
# build.gradle e o wrapper não mudarem, o Docker reaproveita esta camada em
# cache e pula o download de dependências nos próximos builds — só o `COPY
# src` abaixo (e a compilação) precisa rodar de novo quando o código muda.
COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle gradle
RUN chmod +x gradlew
RUN ./gradlew --no-daemon dependencies

COPY src src
RUN ./gradlew --no-daemon build -x test

# Estágio 2: runtime — imagem final só com o JRE e o jar, sem o Gradle/JDK
# de build (bem menor que a imagem de build).
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /app/build/libs/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
