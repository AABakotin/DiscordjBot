package ru.discordj.bot.events.listener.configurator.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.discordj.bot.embed.EmbedFactory;
import ru.discordj.bot.events.listener.configurator.BaseCommand;
import ru.discordj.bot.events.listener.configurator.ConfiguratorError;
import ru.discordj.bot.utility.pojo.Root;

/**
 * Команда для чтения текущей конфигурации.
 * Отображает все настройки бота в текущем канале.
 */
public class ReadConfigCommand extends BaseCommand {
    private static final String EMPTY = "empty";

    /**
     * Отображает текущую конфигурацию бота.
     * Проверяет наличие прав администратора перед выполнением.
     *
     * @param args аргументы команды
     * @param event событие сообщения Discord
     * @param root корневой объект конфигурации
     */
    @Override
    public void execute(String[] args, MessageReceivedEvent event, Root root) {
        if (root.getOwner() == null || root.getOwner().equals(EMPTY)) {
            sendMessage(event, ConfiguratorError.ADMIN_NOT_FOUND.getMessage());
            return;
        }
        
        if (event.getAuthor().getId().equals(root.getOwner())) {
            sendEmbed(event, EmbedFactory.getInstance().createConfigEmbed().embedConfiguration());
        }
    }
} 