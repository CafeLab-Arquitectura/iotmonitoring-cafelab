# Etapa 1: Compilación con Maven y Java 21
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace

# Copiar archivos de Maven Wrapper y dependencias
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copiar código fuente y compilar JAR (omitiendo tests para despliegue rápido)
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# Etapa 2: Imagen final liviana de ejecución (JRE 21)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Crear usuario sin privilegios para seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar artefacto generado desde builder
COPY --from=builder /workspace/target/*.jar app.jar

# Exponer el puerto del microservicio
EXPOSE 8085

# Variables de entorno por defecto
ENV PORT=8085 \
    SPRING_PROFILES_ACTIVE=prod

# Comando de arranque optimizado para contenedores
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
