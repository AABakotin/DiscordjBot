package ru.discordj.bot.utility;

import net.dv8tion.jda.api.entities.Guild;
import ru.discordj.bot.utility.pojo.RulesMessage;
import ru.discordj.bot.utility.pojo.ServerRules;

/**
 * Интерфейс для работы с JSON файлами конфигурации.
 */
public interface IJsonHandler {
    /**
     * Читает глобальную конфигурацию из JSON файла.
     *
     * @return Корневой объект конфигурации
     */
    ServerRules read();

    /**
     * Читает конфигурацию для конкретной гильдии из JSON файла.
     *
     * @param guild Объект гильдии Discord
     * @return Корневой объект конфигурации
     */
    ServerRules read(Guild guild);

    /**
     * Записывает глобальную конфигурацию в JSON файл.
     *
     * @param root Корневой объект конфигурации
     */
    void write(ServerRules root);

    /**
     * Записывает конфигурацию для конкретной гильдии в JSON файл.
     *
     * @param guild Объект гильдии Discord
     * @param root Корневой объект конфигурации
     */
    void write(Guild guild, ServerRules root);

    /**
     * Читает правила из JSON файла.
     *
     * @return Объект с правилами
     */
    RulesMessage readRules();

    /**
     * Записывает правила в JSON файл.
     *
     * @param rules Объект с правилами
     */
    void writeRules(RulesMessage rules);

    /**
     * Читает правила для конкретной гильдии из JSON файла.
     *
     * @param guild Объект гильдии Discord
     * @return Объект с правилами
     */
    RulesMessage readRules(Guild guild);

    /**
     * Записывает правила для конкретной гильдии в JSON файл.
     *
     * @param guild Объект гильдии Discord
     * @param rules Объект с правилами
     */
    void writeRules(Guild guild, RulesMessage rules);
} 