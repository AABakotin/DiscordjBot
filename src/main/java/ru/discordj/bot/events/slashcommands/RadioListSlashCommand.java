package ru.discordj.bot.events.slashcommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.discordj.bot.events.ICommand;
import ru.discordj.bot.utility.JsonParse;
import ru.discordj.bot.utility.pojo.RadioStation;
import ru.discordj.bot.utility.pojo.ServerRules;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Slash-команда для отображения списка доступных радиостанций.
 */
public class RadioListSlashCommand implements ICommand {
    
    @Override
    public String getName() {
        return "radio_list";
    }
    
    @Override
    public String getDescription() {
        return "Показать список доступных радиостанций";
    }
    
    @Override
    public List<OptionData> getOptions() {
        // Команда не имеет параметров
        return new ArrayList<>();
    }
    
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        try {
            // Получаем конфигурацию гильдии
            ServerRules guildConfig = JsonParse.getInstance().read(event.getGuild());
            List<RadioStation> stations = guildConfig.getRadioStations();
            
            if (stations.isEmpty()) {
                event.reply("❌ Список радиостанций пуст. Администраторы могут добавить станции с помощью команды `/radio_add`")
                    .setEphemeral(true)
                    .queue(response -> response.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
                return;
            }
            
            // Создаем красивый эмбед со списком станций
            EmbedBuilder embed = new EmbedBuilder()
                .setTitle("📻 Список доступных радиостанций")
                .setColor(Color.decode("#9370DB")) // Медиум пурпурный цвет
                .setDescription("Для воспроизведения используйте команду `/play name:название`\n")
                .setFooter("Всего станций: " + stations.size() + " | Обновить список: /radio_reload");
            
            // Добавляем информацию о каждой станции
            for (int i = 0; i < stations.size(); i++) {
                RadioStation station = stations.get(i);
                embed.appendDescription(String.format("\n**%d.** %s", i + 1, station.getName()));
            }
            
            // Отправляем эмбед
            event.replyEmbeds(embed.build())
                .setEphemeral(true) // Видно только пользователю
                .queue();
            
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