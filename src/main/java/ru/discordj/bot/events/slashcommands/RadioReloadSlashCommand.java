package ru.discordj.bot.events.slashcommands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.discordj.bot.events.ICommand;
import ru.discordj.bot.utility.JsonParse;
import ru.discordj.bot.utility.pojo.ServerRules;
import ru.discordj.bot.utility.pojo.RadioStation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Slash-команда для перезагрузки списка радиостанций из файла конфигурации.
 */
public class RadioReloadSlashCommand implements ICommand {
    
    @Override
    public String getName() {
        return "radio_reload";
    }
    
    @Override
    public String getDescription() {
        return "Перезагрузить список радиостанций из файла конфигурации";
    }
    
    @Override
    public List<OptionData> getOptions() {
        // Команда не имеет параметров
        return new ArrayList<>();
    }
    
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        try {
            // Проверяем права администратора
            if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                event.reply("❌ Эта команда доступна только администраторам сервера!")
                    .setEphemeral(true)
                    .queue(response -> response.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
                return;
            }
            
            // Форсируем обновление радиостанций из конфигурации по умолчанию
            List<RadioStation> updatedStations = JsonParse.getInstance().reloadRadioStations(event.getGuild());
            
            if (updatedStations == null) {
                event.reply("❌ Произошла ошибка при обновлении списка радиостанций!")
                    .setEphemeral(true)
                    .queue(response -> response.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
                return;
            }
            
            // Отправляем сообщение об успехе
            event.reply("✅ Список радиостанций успешно обновлен! Добавлено **" + updatedStations.size() + "** " + 
                    getStationWordForm(updatedStations.size()))
                .setEphemeral(true)
                .queue(response -> response.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
            
        } catch (Exception e) {
            event.reply("❌ Произошла ошибка при перезагрузке: " + e.getMessage())
                .setEphemeral(true)
                .queue(response -> response.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
        }
    }
    
    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        // Не используется
    }
    
    /**
     * Возвращает правильную форму слова "станция" для числительных
     */
    private String getStationWordForm(int count) {
        int lastDigit = count % 10;
        int lastTwoDigits = count % 100;
        
        if (lastTwoDigits >= 11 && lastTwoDigits <= 19) {
            return "станций";
        }
        
        if (lastDigit == 1) {
            return "станция";
        }
        
        if (lastDigit >= 2 && lastDigit <= 4) {
            return "станции";
        }
        
        return "станций";
    }
} 