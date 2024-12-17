package ru.discordj.bot.events;

import java.util.Collections;
import java.util.List;

import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * Интерфейс для slash-команд с подкомандами
 */
public interface ISlashCommandWithSubcommands extends ISlashcommands {
    /**
     * Возвращает список подкоманд
     */
    List<SubcommandData> getSubcommands();

    /**
     * Стандартная реализация getOptions() для команд с подкомандами
     */
    @Override
    default List<OptionData> getOptions() {
        return Collections.emptyList(); // У команд с подкомандами нет опций верхнего уровня
    }
} 