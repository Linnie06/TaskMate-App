@echo off
echo ====================================
echo TaskMate App - Java Setup Helper
echo ====================================
echo.

echo Checking Java installation...
java -version >nul 2>&1

if %errorlevel% == 0 (
    echo ✓ Java is installed and accessible
    echo.
    echo Current Java version:
    java -version
    echo.
    echo JAVA_HOME: %JAVA_HOME%
    echo.
    echo You can now build the TaskMate app!
    echo Run: gradlew clean build
) else (
    echo ✗ Java is not installed or not in PATH
    echo.
    echo Please follow these steps:
    echo 1. Download JDK 11+ from: https://www.oracle.com/java/technologies/javase-downloads.html
    echo 2. Install the JDK
    echo 3. Set JAVA_HOME environment variable to JDK installation directory
    echo 4. Add %%JAVA_HOME%%\bin to your PATH
    echo 5. Restart this terminal and run this script again
    echo.
    echo Example JAVA_HOME: C:\Program Files\Java\jdk-11.0.x
)

echo.
pause