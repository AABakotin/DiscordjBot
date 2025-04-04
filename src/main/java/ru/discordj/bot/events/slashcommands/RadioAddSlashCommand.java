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
 * Slash-команда для добавления новой радиостанции в список.
 * Доступна только администраторам сервера.
 */
public class RadioAddSlashCommand implements ICommand {
    
    @Override
    public String getName() {
        return "radio_add";
    }
    
    @Override
    public String getDescription() {
        return "Добавить новую радиостанцию в список";
    }
    
    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        
        options.add(new OptionData(OptionType.STRING, "name", "Название радиостанции", true));
        options.add(new OptionData(OptionType.STRING, "url", "URL потока радиостанции", true));
        options.add(new OptionData(OptionType.STRING, "description", "Описание радиостанции", true));
        options.add(new OptionData(OptionType.STRING, "category", "Категория (Популярные/Record/Другие)", true));
        
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
        
        // Получаем параметры из команды
        String name = event.getOption("name").getAsString();
        String url = event.getOption("url").getAsString();
        String description = event.getOption("description").getAsString();
        String category = event.getOption("category").getAsString();
        
        try {
            // Проверяем, существует ли уже радиостанция с таким названием
            if (RadioSlashCommand.hasStation(name)) {
                event.reply("❌ Радиостанция с названием **" + name + "** уже существует!")
                    .setEphemeral(true)
                    .queue();
                return;
            }
            
            // Проверяем URL на корректность
            if (!url.startsWith("http")) {
                event.reply("❌ URL должен начинаться с http:// или https://")
                    .setEphemeral(true)
                    .queue();
                return;
            }
            
            // Проверяем категорию
            if (!category.equals("Популярные") && !category.equals("Record") && !category.equals("Другие")) {
                event.reply("❌ Категория должна быть одной из: Популярные, Record, Другие")
                    .setEphemeral(true)
                    .queue();
                return;
            }
            
            // Добавляем радиостанцию
            RadioSlashCommand.addRadioStation(name, url, description, category);
            
            // Формируем сообщение об успешном добавлении
            StringBuilder successMessage = new StringBuilder();
            successMessage.append("✅ Радиостанция успешно добавлена!\n\n");
            successMessage.append("📻 **").append(name).append("**\n");
            successMessage.append("🔗 URL: `").append(url).append("`\n");
            successMessage.append("📝 Описание: ").append(description).append("\n");
            successMessage.append("🏷️ Категория: ").append(category).append("\n\n");
            
            // Добавляем информацию о количестве станций
            int totalStations = RadioSlashCommand.getStationsCount();
            successMessage.append("📊 Всего радиостанций: ").append(totalStations);
            
            // Предупреждение о лимите Discord
            if (totalStations > 25) {
                successMessage.append("\n⚠️ Внимание: в меню будут отображаться только первые 25 станций из ")
                    .append(totalStations).append(" из-за ограничений Discord.");
            }
            
            // Отправляем сообщение с результатом операции
            event.reply(successMessage.toString())
                .setEphemeral(true)
                .queue();
            
        } catch (Exception e) {
            // В случае ошибки выводим информацию о ней
            System.err.println("Ошибка добавления радиостанции: " + e.getMessage());
            e.printStackTrace();
            
            event.reply("❌ Произошла ошибка при добавлении радиостанции: " + e.getMessage())
                .setEphemeral(true)
                .queue();
        }
    }
} 