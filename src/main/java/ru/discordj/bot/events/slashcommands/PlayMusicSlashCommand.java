package ru.discordj.bot.events.slashcommands;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import ru.discordj.bot.events.ICommand;
import ru.discordj.bot.lavaplayer.PlayerManager;
import ru.discordj.bot.utility.MessageCollector;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Обработчик slash-команды для воспроизведения музыки.
 * Позволяет добавлять треки в очередь воспроизведения по URL или поисковому запросу.
 * Поддерживает автоматическое подключение к голосовому каналу.
 */
public class PlayMusicSlashCommand implements ICommand {
    private final Map<String, MessageCollector> activeCollectors = new HashMap<>();

    public Map<String, MessageCollector> getActiveCollectors() {
        return activeCollectors;
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Play a song";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        StringSelectMenu sourceMenu = StringSelectMenu.create("play_source")
            .setPlaceholder("Выберите источник")
            .addOption("Прямая ссылка", "auto", "Воспроизвести музыку по ссылке")
            .addOption("YouTube", "youtube", "Поиск на YouTube")
            .addOption("Twitch", "twitch", "Стрим с Twitch")
            .addOption("Bandcamp", "bandcamp", "Поиск на Bandcamp")
            .build();

        event.reply("Выберите источник музыки:")
            .addActionRow(sourceMenu)
            .setEphemeral(true)
            .queue();
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals("play_source")) {
            String source = event.getValues().get(0);
            
            if (!validateVoiceState(event)) {
                return;
            }
            
            connectToVoiceChannel(event);

            // Сохраняем сообщение с меню для последующего удаления
            event.getMessage().delete().queue();

            event.reply("Введите поисковый запрос или ссылку в чат (у вас есть 30 секунд)")
                .setEphemeral(true)
                .queue(response -> {
                    // Создаем коллектор сообщений
                    MessageCollector collector = MessageCollector.create(
                        event.getChannel(),
                        event.getUser(),
                        message -> {
                            String query = message.getContentRaw();
                            String searchQuery;
                            
                            switch (source) {
                                case "youtube":
                                    searchQuery = "ytsearch:" + query;
                                    break;
                                case "twitch":
                                    if (!query.startsWith("http")) {
                                        message.delete().queue();
                                        response.deleteOriginal().queue();
                                        event.getChannel().asTextChannel().sendMessage("Для Twitch необходимо указать прямую ссылку на стрим")
                                            .queue(msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
                                        return;
                                    }
                                    searchQuery = query;
                                    break;
                                case "bandcamp":
                                    searchQuery = "bcsearch:" + query;
                                    break;
                                default: // auto
                                    searchQuery = query.startsWith("http") ? query : "ytsearch:" + query;
                                    break;
                            }

                            message.delete().queue(); // Удаляем сообщение с запросом
                            response.deleteOriginal().queue(); // Удаляем сообщение "Введите поисковый запрос"
                            
                            // Используем play вместо loadAndPlay для отображения плеера
                            PlayerManager.getInstance().play(
                                event.getChannel().asTextChannel(),
                                searchQuery
                            );
                        },
                        30
                    );

                    activeCollectors.put(event.getUser().getId(), collector);
                });
        }
    }

    private boolean validateVoiceState(IReplyCallback event) {
        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inAudioChannel()) {
            event.reply("You need to be in a voice channel").setEphemeral(true).queue();
            return false;
        }
        return true;
    }

    private void connectToVoiceChannel(IReplyCallback event) {
        AudioManager audioManager = event.getGuild().getAudioManager();
        Member member = event.getMember();
        
        if (!audioManager.isConnected()) {
            audioManager.openAudioConnection(member.getVoiceState().getChannel());
        }
    }
}