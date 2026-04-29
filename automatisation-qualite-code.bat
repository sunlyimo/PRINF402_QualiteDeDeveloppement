SET MAVEN_HOME=c:\data\maven
SET PATH=%MAVEN_HOME%\bin;%PATH%
call mvn clean verify site
pause