# Usa un'immagine JDK come base
FROM eclipse-temurin:21-jdk

# Crea una directory per l'app
WORKDIR /app

# Copia i file del progetto
COPY . .

# Compila il progetto
RUN ./mvnw clean package -DskipTests

# Espone la porta 8081
EXPOSE 8081

# Esegue il jar
CMD ["java", "-jar", "target/CAPSTONE_BACKEND-0.0.1-SNAPSHOT.jar"]