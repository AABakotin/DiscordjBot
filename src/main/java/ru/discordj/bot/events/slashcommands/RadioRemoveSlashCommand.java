package ru.discordj.bot.events.slashcommands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.discordj.bot.events.ICommand;
import ru.discordj.bot.utility.JsonParse;
import ru.discordj.bot.utility.pojo.RadioStation;
import ru.discordj.bot.utility.pojo.ServerRules;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Slash-команда для удаления радиостанции.
 */
public class RadioRemoveSlashCommand implements ICommand {
    
    @Override
    public String getName() {
        return "radio_remove";
    }
    
    @Override
    public String getDescription() {
        return "Удалить радиостанцию";
    }
    
    @Override
    public List<OptionData> getOptions() {
        return Collections.singletonList(
            new OptionData(OptionType.STRING, "name", "Название радиостанции для удаления", true)
        );
    }
    
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        try {
            // Проверяем права администратора
            if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                event.reply("❌ Эта команда доступна только администраторам сервера!")
                    .setEphemeral(true)
                    .queue(response -> response.deleteOriginal().queueAfter(10, TimeUnit.SECONDS));
                return;
            }
            
            // Получаем название станции для удаления
            String name = event.getOption("name").getAsString();
            
            // Получаем конфигурацию гильдии
            ServerRules config = JsonParse.getInstance().read(event.getGuild());
            
            // Ищем станцию в списке
            RadioStation stationToRemove = null;
            for (RadioStation station : config.getRadioStations()) {
                if (station.getName().equalsIgnoreCase(name)) {
                    stationToRemove = station;
                    break;
                }
            }
            
            // Проверяем, существует ли станция
            if (stationToRemove == null) {
                event.reply("❌ Радиостанция **" + name + "** не найдена!")
                    .setEphemeral(true)
                    .queue(response -> response.deleteOriginal().queueAfter(10, TimeUnit.SECONDS));
                return;
            }
            
            // Удаляем станцию
            config.getRadioStations().remove(stationToRemove);
            
            // Сохраняем конфигурацию
            JsonParse.getInstance().write(event.getGuild(), config);
            
            // Отправляем сообщение об успехе
            event.reply("✅ Радиостанция **" + name + "** успешно удалена!")
                .setEphemeral(true)
                .queue(response -> response.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
            
        } catch (Exception e) {
            event.reply("❌ Произошла ошибка: " + e.getMessage())
                .setEphemeral(true)
                .queue(response -> response.deleteOriginal().queueAfter(10, TimeUnit.SECONDS));
        }
    }
    
    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        // Не используется
    }
} 