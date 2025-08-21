@echo off
REM Windows 환경변수 설정 스크립트

echo PostgreSQL 환경변수를 설정합니다...

REM PostgreSQL 설정
set POSTGRES_USER=app
set POSTGRES_PASSWORD=app_pw
set POSTGRES_DB=db_generator
set POSTGRES_HOST=localhost
set POSTGRES_PORT=5432

REM PgAdmin 설정
set PGADMIN_EMAIL=admin@example.com
set PGADMIN_PASSWORD=admin_pw
set PGADMIN_PORT=5050

REM 애플리케이션 설정
set APP_PORT=8080

REM ACR 설정
set ACR_NAME=YOUR_ACR_NAME

REM 이미지 이름 설정
set IMAGE_NAME=YOUR_IMAGE_NAME

echo 환경변수 설정이 완료되었습니다.
echo.
echo 설정된 환경변수:
echo POSTGRES_USER=%POSTGRES_USER%
echo POSTGRES_DB=%POSTGRES_DB%
echo POSTGRES_HOST=%POSTGRES_HOST%
echo POSTGRES_PORT=%POSTGRES_PORT%
echo APP_PORT=%APP_PORT%
echo.
echo 이제 다음 명령어로 애플리케이션을 실행할 수 있습니다:
echo gradlew.bat bootRun
