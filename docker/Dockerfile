#######################
# ANGULAR COMPILATION #
#######################
FROM node:18-alpine AS frontend-build

WORKDIR /app/frontend

# 1. Copiar solo los archivos esenciales primero
COPY frontend/package.json frontend/package-lock.json ./

# 2. Configurar npm
RUN npm config set cache /tmp/.npm && \
    npm config set update-notifier false && \
    npm config set fund false

# 3. Instalar dependencias básicas
RUN npm ci --no-optional --no-audit --progress=false

# 4. Copiar el resto del código fuente
COPY frontend/ ./

# # 5. Instalar file-saver y sus tipos
RUN npm install 

# 6. Modificar angular.json de forma segura
RUN echo 'Modificando angular.json...' && \
    # Eliminar budgets completamente
    sed -i '/"budgets": \[/,/\]/d' angular.json && \
    # Agregar allowedCommonJsDependencies
    sed -i '/"build": {/a \            "allowedCommonJsDependencies": ["file-saver"],' angular.json && \
    # Aumentar límites de budgets si existen
    sed -i 's/"maximumWarning": ".*"/"maximumWarning": "5mb"/g' angular.json && \
    sed -i 's/"maximumError": ".*"/"maximumError": "5mb"/g' angular.json

# 7. Construir la aplicación Angular
RUN npm run build -- --configuration production --base-href=/new/ --no-prerender

# =================================================
# BUILD STAGE: COMPILATION AND PACKAGING
# =================================================
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copiar archivos del backend
COPY backend/pom.xml .
COPY backend/src ./src

# Copiar el frontend construido
COPY --from=frontend-build /app/frontend/dist/frontend/browser/ src/main/resources/static/new/

# Build Java
RUN mvn clean package -DskipTests

# =================================================
# RUNTIME STAGE
# =================================================
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8443

ENTRYPOINT ["java", "-jar", "app.jar"]