@REM Maven Wrapper for Windows
@echo off

set MAVEN_PROJECTBASEDIR=%~dp0
set MAVEN_WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"

if not exist %MAVEN_WRAPPER_JAR% (
    echo Downloading Maven Wrapper...
    powershell -Command "(New-Object Net.WebClient).DownloadFile('%WRAPPER_URL:"=%', '%MAVEN_WRAPPER_JAR:"=%')"
)

if exist %MAVEN_WRAPPER_JAR% (
    set JAVA_CMD=java
    if defined JAVA_HOME set JAVA_CMD="%JAVA_HOME%\bin\java"
    %JAVA_CMD% %MAVEN_OPTS% -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR%" -jar %MAVEN_WRAPPER_JAR% %*
) else (
    echo Error: Could not download maven-wrapper.jar
    echo Install Maven manually: https://maven.apache.org/install.html
    echo Then run: mvn spring-boot:run
)
