# 베이스 이미지 설정
FROM openjdk:17-jdk-slim

# 포트 설정
EXPOSE 8080

# 애플리케이션 JAR 파일 추가
ADD ./build/libs/demo-0.0.1-SNAPSHOT.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]