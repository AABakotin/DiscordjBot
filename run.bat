@echo off
chcp 65001 > nul
title Discord Bot
echo Starting DiscordJBot with UTF-8 encoding...
echo.

java -Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8 -jar DiscordjBot-1.0-jar-with-dependencies.jar %*

echo Bot finished working.
pause 