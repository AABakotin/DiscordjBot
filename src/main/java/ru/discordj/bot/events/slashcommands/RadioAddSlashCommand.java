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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Slash-команда для добавления новой радиостанции.
 */
public class RadioAddSlashCommand implements ICommand {
    
    @Override
    public String getName() {
        return "radio_add";
    }
    
    @Override
    public String getDescription() {
        return "Добавить новую радиостанцию";
    }
    
    @Override
    public List<OptionData> getOptions() {
        return Arrays.asList(
            new OptionData(OptionType.STRING, "name", "Название радиостанции", true),
            new OptionData(OptionType.STRING, "url", "URL потока радиостанции", true)
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
            
            // Получаем параметры
            String name = event.getOption("name").getAsString();
            String url = event.getOption("url").getAsString();
            
            // Проверяем URL на валидность
            if (!url.startsWith("http")) {
                event.reply("❌ URL должен начинаться с http:// или https://")
                    .setEphemeral(true)
                    .queue(response -> response.deleteOriginal().queueAfter(10, TimeUnit.SECONDS));
                return;
            }
            
            // Получаем конфигурацию гильдии
            ServerRules config = JsonParse.getInstance().read(event.getGuild());
            
            // Проверяем, существует ли уже станция с таким именем
            if (config.getRadioStations().stream().anyMatch(s -> s.getName().equalsIgnoreCase(name))) {
                event.reply("❌ Радиостанция с названием **" + name + "** уже существует!")
                    .setEphemeral(true)
                    .queue(response -> response.deleteOriginal().queueAfter(10, TimeUnit.SECONDS));
                return;
            }
            
            // Создаем новую радиостанцию
            RadioStation newStation = new RadioStation(name, url);
            
            // Добавляем станцию в конфигурацию
            config.getRadioStations().add(newStation);
            
            // Сохраняем конфигурацию
            JsonParse.getInstance().write(event.getGuild(), config);
            
            // Отправляем сообщение об успехе
            event.reply("✅ Радиостанция **" + name + "** успешно добавлена!")
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