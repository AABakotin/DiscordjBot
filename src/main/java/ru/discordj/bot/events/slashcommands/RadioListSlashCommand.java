package ru.discordj.bot.events.slashcommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.discordj.bot.events.ICommand;

import java.awt.Color;
import java.util.*;
import java.util.stream.Collectors;

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
        return Collections.emptyList();
    }
    
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        try {
            // Получаем список всех радиостанций по категориям
            Map<String, List<RadioSlashCommand.RadioStation>> stationsByCategory = 
                RadioSlashCommand.getStationsByCategory();
            
            // Если список пуст, сообщаем об этом
            if (stationsByCategory.isEmpty()) {
                event.reply("❌ Список радиостанций пуст!")
                    .setEphemeral(true)
                    .queue();
                return;
            }
            
            // Создаем эмбед с информацией о радиостанциях
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("📻 Список доступных радиостанций");
            embed.setColor(Color.BLUE);
            embed.setDescription("Всего радиостанций: " + RadioSlashCommand.getStationsCount() + 
                                "\nИспользуйте команду `/radio` для прослушивания");
            
            // Добавляем поля с категориями радиостанций
            for (Map.Entry<String, List<RadioSlashCommand.RadioStation>> entry : stationsByCategory.entrySet()) {
                String category = entry.getKey();
                List<RadioSlashCommand.RadioStation> stations = entry.getValue();
                
                // Строим список станций этой категории
                String stationsList = stations.stream()
                    .map(station -> "• " + station.getName())
                    .collect(Collectors.joining("\n"));
                
                // Добавляем поле с категорией
                embed.addField("🏷️ " + category + " (" + stations.size() + ")", 
                              stationsList, 
                              true);
            }
            
            // Добавляем совет
            embed.setFooter("💡 Администраторы могут добавлять/удалять станции через команды /radio_add и /radio_remove");
            
            // Отправляем эмбед
            event.replyEmbeds(embed.build())
                .queue();
            
        } catch (Exception e) {
            // В случае ошибки выводим информацию о ней
            System.err.println("Ошибка при отображении списка радиостанций: " + e.getMessage());
            e.printStackTrace();
            
            event.reply("❌ Произошла ошибка при получении списка радиостанций: " + e.getMessage())
                .setEphemeral(true)
                .queue();
        }
    }
} 