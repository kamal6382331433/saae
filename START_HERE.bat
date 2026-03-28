@echo off
cls
echo.
echo ================================================
echo    SAAE - Students Academic Analysis Engine
echo ================================================
echo.
echo This will start your application on port 8081
echo.
echo After starting, open your browser and go to:
echo    http://localhost:8081/
echo.
echo Login with:
echo    Username: admin
echo    Password: admin123
echo.
echo ================================================
echo.
pause

echo Starting application...
echo.

cd /d "%~dp0"

REM Check if local Maven exists, otherwise check wrapper, otherwise use system Maven
if exist "%~dp0apache-maven-3.9.6\bin\mvn.cmd" (
    echo Using local Maven...
    call "%~dp0apache-maven-3.9.6\bin\mvn.cmd" spring-boot:run
) else if exist mvnw.cmd (
    echo Using Maven Wrapper...
    call mvnw.cmd spring-boot:run
) else (
    echo Using system Maven...
    call mvn spring-boot:run
)

if %errorlevel% neq 0 (
    echo.
    echo ================================================
    echo ERROR: Application failed to start!
    echo ================================================
    echo.
    echo Possible reasons:
    echo 1. Maven is not installed
    echo 2. MySQL is not running
    echo 3. Port 8081 is already in use
    echo.
    echo Try these:
    echo - Start MySQL service
    echo - Close any apps using port 8081
    echo - Check internet connection (Maven needs to download dependencies)
    echo.
    pause
    exit /b 1
)

pause
