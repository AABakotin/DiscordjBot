#!/bin/bash

echo "Starting DiscordJBot with UTF-8 encoding..."
echo

java -Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8 -jar DiscordjBot-jar-with-dependencies.jar "$@"

echo
echo "Bot stopped." 