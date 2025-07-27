# Dockerfile
FROM amazoncorretto:17-alpine-jdk

# 빌드된 JAR 파일을 컨테이너 내부로 복사
COPY ./build/libs/happy-happy-backend-0.0.1-SNAPSHOT.jar app.jar

# JAR 실행
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/app.jar"]
