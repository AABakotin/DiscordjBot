@echo off
chcp 65001 > nul
title Discord Bot
echo Starting DiscordJBot with UTF-8 encoding...
echo.

start /min cmd /c "java -Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8 -jar ./DiscordjBot-1.0-jar-with-dependencies.jar %* && pause"

echo Bot has been started in minimized window.
timeout /t 3 > nul 