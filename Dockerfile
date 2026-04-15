# Giai đoạn 1: Build ứng dụng
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy file cấu hình maven trước để tận dụng cache
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy mã nguồn và đóng gói
COPY src ./src
RUN mvn clean package -DskipTests

# Giai đoạn 2: Chạy ứng dụng
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy file jar từ giai đoạn build
COPY --from=build /app/target/*.jar app.jar

# Khai báo cổng ứng dụng
EXPOSE 8080

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
