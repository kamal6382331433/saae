@echo off
cls
echo ========================================
echo  SAAE - DIRECT RUN (Bypass Eclipse)
echo ========================================
echo.
echo This script will run your application
echo using Maven directly, bypassing Eclipse.
echo.
echo After starting, open: http://localhost:8081/
echo Login: admin / admin123
echo.
echo ========================================
pause

cd /d "%~dp0"

echo.
echo Starting application with Maven...
echo.

REM Using Maven directly to compile and run
if exist "%~dp0apache-maven-3.9.6\bin\mvn.cmd" (
    echo Using local Maven...
    call "%~dp0apache-maven-3.9.6\bin\mvn.cmd" clean spring-boot:run -DskipTests
) else (
    call mvn clean spring-boot:run -DskipTests
)

if %errorlevel% neq 0 (
    echo.
    echo ========================================
    echo  ERROR!
    echo ========================================
    echo.
    echo If you see "mvn is not recognized":
    echo   Maven is not installed or not in PATH
    echo.
    echo If you see other errors:
    echo   Check MySQL is running
    echo   Check port 8081 is free
    echo.
    pause
    exit /b 1
)

pause
