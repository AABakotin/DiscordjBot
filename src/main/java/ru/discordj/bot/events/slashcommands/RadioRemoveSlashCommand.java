package ru.discordj.bot.events.slashcommands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.discordj.bot.events.ICommand;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Slash-команда для удаления радиостанции из списка.
 * Доступна только администраторам сервера.
 */
public class RadioRemoveSlashCommand implements ICommand {
    
    @Override
    public String getName() {
        return "radio_remove";
    }
    
    @Override
    public String getDescription() {
        return "Удалить радиостанцию из списка";
    }
    
    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "name", "Название радиостанции для удаления", true));
        return options;
    }
    
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // Проверяем, имеет ли пользователь права администратора
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("❌ Эта команда доступна только администраторам сервера.")
                .setEphemeral(true)
                .queue(response -> {
                    response.deleteOriginal().queueAfter(10, TimeUnit.SECONDS);
                });
            return;
        }
        
        // Получаем название радиостанции из команды
        String stationName = event.getOption("name").getAsString();
        
        try {
            // Проверяем, существует ли радиостанция с таким названием
            if (!RadioSlashCommand.hasStation(stationName)) {
                event.reply("❌ Радиостанция с названием **" + stationName + "** не найдена!")
                    .setEphemeral(true)
                    .queue();
                return;
            }
            
            // Запоминаем количество радиостанций до удаления
            int previousCount = RadioSlashCommand.getStationsCount();
            
            // Удаляем радиостанцию
            RadioSlashCommand.removeRadioStation(stationName);
            
            // Получаем новое количество радиостанций
            int newCount = RadioSlashCommand.getStationsCount();
            
            // Формируем сообщение об успешном удалении
            StringBuilder successMessage = new StringBuilder();
            successMessage.append("✅ Радиостанция **").append(stationName).append("** успешно удалена!\n\n");
            successMessage.append("📊 Статистика:\n");
            successMessage.append("• Было: ").append(previousCount).append(" станций\n");
            successMessage.append("• Стало: ").append(newCount).append(" станций");
            
            // Отправляем сообщение с результатом операции
            event.reply(successMessage.toString())
                .setEphemeral(true)
                .queue();
            
        } catch (Exception e) {
            // В случае ошибки выводим информацию о ней
            System.err.println("Ошибка удаления радиостанции: " + e.getMessage());
            e.printStackTrace();
            
            event.reply("❌ Произошла ошибка при удалении радиостанции: " + e.getMessage())
                .setEphemeral(true)
                .queue();
        }
    }
} 