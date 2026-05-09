@echo off
setlocal
set "ROOT=%~dp0"
set "JAVA_HOME=%ROOT%..\\seasonal-villages-fabric\\.tools\\jdk-21"
set "PATH=%JAVA_HOME%\bin;%PATH%"
call "%ROOT%gradlew.bat" %*
exit /b %ERRORLEVEL%
