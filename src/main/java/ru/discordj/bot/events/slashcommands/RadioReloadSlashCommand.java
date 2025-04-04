package ru.discordj.bot.events.slashcommands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.discordj.bot.events.ICommand;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Slash-команда для перезагрузки списка радиостанций из JSON-файла.
 * Доступна только администраторам сервера.
 */
public class RadioReloadSlashCommand implements ICommand {
    
    @Override
    public String getName() {
        return "radio_reload";
    }
    
    @Override
    public String getDescription() {
        return "Перезагрузить список радиостанций из JSON-файла";
    }
    
    @Override
    public List<OptionData> getOptions() {
        return Collections.emptyList();
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
        
        try {
            // Запоминаем текущее количество радиостанций
            int previousStationsCount = RadioSlashCommand.getStationsCount();
            
            // Перезагружаем список радиостанций
            RadioSlashCommand.reloadStations();
            
            // Получаем новое количество радиостанций
            int newStationsCount = RadioSlashCommand.getStationsCount();
            
            // Формируем сообщение об успешной перезагрузке
            StringBuilder successMessage = new StringBuilder();
            successMessage.append("✅ Список радиостанций успешно перезагружен из файла!\n");
            successMessage.append("📊 Статистика:\n");
            successMessage.append("• Было: ").append(previousStationsCount).append(" станций\n");
            successMessage.append("• Стало: ").append(newStationsCount).append(" станций");
            
            // Если количество станций превышает лимит Discord (25), добавляем предупреждение
            if (newStationsCount > 25) {
                successMessage.append("\n⚠️ Внимание: в меню будут отображаться только первые 25 станций из ")
                    .append(newStationsCount).append(" из-за ограничений Discord.");
            }
            
            // Отправляем сообщение с результатом операции
            event.reply(successMessage.toString())
                .setEphemeral(true)
                .queue();
            
        } catch (Exception e) {
            // В случае ошибки выводим информацию о ней
            System.err.println("Ошибка перезагрузки радиостанций: " + e.getMessage());
            e.printStackTrace();
            
            event.reply("❌ Произошла ошибка при перезагрузке радиостанций: " + e.getMessage())
                .setEphemeral(true)
                .queue();
        }
    }
} 