# Liberica JDK 17 버전을 기반 이미지로 사용
FROM bellsoft/liberica-openjdk-debian:17

# 컨테이너 내부의 작업 디렉토리 설정
WORKDIR /app

# Gradle 빌드를 통해 생성된 JAR 파일을 컨테이너 내부로 복사
# build/libs/ 디렉토리에 있는 모든 JAR 파일을 app.jar라는 이름으로 복사
COPY build/libs/*.jar app.jar

# 컨테이너 실행 시 자동으로 실행될 명령어 설정
# java -jar app.jar 명령어를 실행하여 스프링부트 애플리케이션 시작
ENTRYPOINT ["java", "-jar", "app.jar"]

# 컨테이너가 사용할 포트 명시 (문서화 목적)
# 실제 포트 바인딩은 docker run 명령어의 -p 옵션으로 설정
EXPOSE 8080