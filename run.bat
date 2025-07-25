@echo off
chcp 65001 > nul
title Discord Bot
echo Starting DiscordJBot with UTF-8 encoding...
echo.

:loop
REM Проверяем, есть ли флаг обновления
if exist update.flag (
    echo Обнаружено обновление. Обновляю jar...
    if exist update.jar (
        del DiscordjBot-jar-with-dependencies.jar
        ren update.jar DiscordjBot-jar-with-dependencies.jar
        echo JAR успешно обновлён.
    ) else (
        echo update.jar не найден! Обновление невозможно.
    )
    del update.flag
    echo Обновление завершено.
)
java -Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8 -jar DiscordjBot-jar-with-dependencies.jar
echo Bot has stopped or was updated. Restarting...
timeout /t 2
goto loop 