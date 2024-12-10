@echo off
setlocal


:: test if the path of the system use UNC path (starts with '\\')
:: cd /d "%~dp0" 2>nul || pushd "%~dp0"
echo.
echo * test if the current directory matches the script directory (UNC path problem) ... 
:: Get the current script's directory
set "currentDir=%~dp0"
:: Check if the path is a UNC path (starts with '\\')
echo %currentDir% | findstr /i "^\\\\.*" >nul
echo   current directory: %currentDir%
if %errorlevel% equ 0 (
    rem If it's a UNC path, inform the user and do not attempt to cd or pushd
    echo   UNC path detected: %currentDir%
    echo   CMD.EXE cannot set the current directory to a UNC path.
	echo   there for CMD.EXE changed automatically directory to: %CD% 
	echo   changing the directory back to the real script directory
    pushd "%~dp0"
) else (
    echo   UNC path not detected
    rem If it's not a UNC path, use the normal cd command
    cd /d "%currentDir%" 2>nul || pushd "%currentDir%"
)
:: Show the current directory after the operation
echo   Current working directory: %CD%
:: Check if the file run_executable.bat exists in the current directory to check if the current dir is OK
if exist "run_executable.bat" (
    echo   OK.
) else (
    echo   FAIL.
    pause
    exit /b
)


:: Check if Java is installed
echo. 
echo * Checking for Java installation...
java -version >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo   Java is not installed. Please follow the provided instructions to install Java.
	echo   FAIL.
    pause
    exit /b
) else (
    echo   OK.
)


:: Check Java version
echo. 
echo * Checking Java version... 
echo   If the program is not working as expected, please verify that your Java version 
echo   is equal to or greater than the version specified in the installation instructions.
java -version


:: Check if "Models" directory exists
echo. 
echo * Checking if the "Models" directory exists...
if not exist ".\Models" (
    echo   Error: The "Models" directory is missing. 
    echo   You probably did not run the executable from the project directory.
	echo   FAIL.
    pause
    exit /b
) else (
    echo   OK.
)


:: Check if executable JAR exists
echo. 
echo * Checking if executable JAR file exists...
if not exist ".\executable.jar" (
    echo   Error: The executable JAR file "executable.jar" is missing.
    echo   Please ensure the executable JAR file is in the same directory as this batch file.
	echo   FAIL.
    pause
    exit /b
) else (
    echo   OK.
)


:: Run the executable JAR
echo. 
echo * Running the executable JAR file...
javaw -jar ".\executable.jar" 

if %ERRORLEVEL% neq 0 (
    echo   Error: Unable to run the JAR file. Please check the JAR file and ensure Java 17 or higher is installed.
	echo   FAIL.
	popd
    pause
    exit /b
) else (
    echo   OK.
	popd
)

endlocal
