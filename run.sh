#!/bin/bash

while true; do
    echo "Starting DiscordJBot with UTF-8 encoding..."
    echo

    # Проверка наличия флага обновления
    if [ -f update.flag ]; then
        echo "Обнаружено обновление. Обновляю jar..."
        if [ -f update.jar ]; then
            rm -f DiscordjBot-jar-with-dependencies.jar
            mv update.jar DiscordjBot-jar-with-dependencies.jar
            echo "JAR успешно обновлён."
        else
            echo "update.jar не найден! Обновление невозможно."
        fi
        rm -f update.flag
        echo "Обновление завершено."
    fi

    java -Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8 -jar DiscordjBot-jar-with-dependencies.jar "$@"

    echo
    echo "Bot has stopped or was updated. Restarting..."
    sleep 2
done 