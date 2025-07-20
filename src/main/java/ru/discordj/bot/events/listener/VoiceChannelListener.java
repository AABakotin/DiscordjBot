package ru.discordj.bot.events.listener;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.lavaplayer.PlayerManager;
import ru.discordj.bot.service.VoiceChannelService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Слушатель событий голосовых каналов.
 * Отслеживает покидание участников из голосового канала и отключает бота, 
 * если он остался один в течение 60 секунд.
 */
public class VoiceChannelListener extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(VoiceChannelListener.class);
    private static final int DISCONNECT_DELAY_SECONDS = 60;
    
    // Для каждой гильдии храним запланированную задачу отключения
    private final Map<Long, ScheduledFuture<?>> disconnectTasks = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final VoiceChannelService voiceService = new VoiceChannelService();

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        // GuildVoiceUpdateEvent - это общее событие, охватывающее как присоединение, так и выход из канала
        
        Guild guild = event.getGuild();
        AudioManager audioManager = guild.getAudioManager();
        
        // Проверяем, подключен ли бот к голосовому каналу
        if (!audioManager.isConnected()) {
            return;
        }
        
        // Получаем голосовой канал, к которому подключен бот
        VoiceChannel botChannel = audioManager.getConnectedChannel().asVoiceChannel();
        
        // Логгируем событие
        String memberName = event.getMember().getEffectiveName();
        String guildName = guild.getName();
        String channelName = botChannel.getName();
        
        if (event.getChannelJoined() != null && event.getChannelJoined().equals(botChannel)) {
            logger.info("Юзер {} присоединился к каналу {} в гильдии {}", 
                    memberName, channelName, guildName);
            
            // Если это не бот
            if (!event.getMember().getUser().isBot()) {
                // Если кто-то присоединился к каналу, отменяем задачу отключения если она существует
                cancelDisconnectTask(guild.getIdLong());
                
                // Отправляем сообщение, что отключение отменено
                TextChannel textChannel = PlayerManager.getInstance()
                    .getGuildMusicManager(guild)
                    .getTrackScheduler()
                    .getTextChannel();
                
                if (textChannel != null && disconnectTasks.containsKey(guild.getIdLong())) {
                    textChannel.sendMessage("✅ **Отключение отменено!** В канале появился слушатель.")
                        .queue(message -> message.delete().queueAfter(5, TimeUnit.SECONDS));
                }
            }
        }
        
        if (event.getChannelLeft() != null && event.getChannelLeft().equals(botChannel)) {
            logger.info("Юзер {} покинул канал {} в гильдии {}", 
                    memberName, channelName, guildName);
            
            // Проверяем, не остался ли бот один в канале (только если ушел не бот)
            if (!event.getMember().getUser().isBot()) {
                checkIfAloneAndScheduleDisconnect(guild, botChannel);
            }
        }
    }
    
    /**
     * Отменяет запланированную задачу отключения для гильдии, если такая существует
     */
    private void cancelDisconnectTask(long guildId) {
        ScheduledFuture<?> task = disconnectTasks.get(guildId);
        if (task != null && !task.isDone()) {
            task.cancel(false);
            disconnectTasks.remove(guildId);
            logger.info("Задача отключения отменена для гильдии {}, так как слушатели присоединились к каналу", guildId);
        }
    }
    
    /**
     * Проверяет, не остался ли бот один в голосовом канале, и планирует отключение через 60 секунд
     */
    private void checkIfAloneAndScheduleDisconnect(Guild guild, VoiceChannel voiceChannel) {
        // Получаем список участников в голосовом канале
        if (voiceService.isBotAlone(voiceChannel)) {
            // Отменяем предыдущую задачу, если она существует
            cancelDisconnectTask(guild.getIdLong());
            
            // Получаем текстовый канал для отправки предупреждения
            var guildMusicManager = PlayerManager.getInstance().getGuildMusicManager(guild);
            TextChannel textChannel = guildMusicManager.getTrackScheduler().getTextChannel();
            
            // Отправляем предупреждение, что бот будет отключен через DISCONNECT_DELAY_SECONDS секунд
            if (textChannel != null) {
                textChannel.sendMessage("⚠️ **Внимание!** Бот остался один в канале и будет отключен через " 
                        + DISCONNECT_DELAY_SECONDS + " секунд, если никто не присоединится.")
                    .queue(message -> message.delete().queueAfter(DISCONNECT_DELAY_SECONDS, TimeUnit.SECONDS));
            }
            
            // Создаем новую задачу отключения
            ScheduledFuture<?> disconnectTask = scheduler.schedule(() -> {
                disconnectBot(guild, voiceChannel);
                disconnectTasks.remove(guild.getIdLong());
            }, DISCONNECT_DELAY_SECONDS, TimeUnit.SECONDS);
            
            // Сохраняем задачу в Map
            disconnectTasks.put(guild.getIdLong(), disconnectTask);
            
            logger.info("Бот остался один в канале {} гильдии {}. Запланировано отключение через {} секунд", 
                    voiceChannel.getName(), guild.getName(), DISCONNECT_DELAY_SECONDS);
        }
    }
    
    /**
     * Отключает бота от голосового канала и останавливает музыку
     */
    private void disconnectBot(Guild guild, VoiceChannel voiceChannel) {
        // Проверяем, все еще ли бот один в канале
        if (!voiceService.isBotAlone(voiceChannel)) {
            logger.info("Disconnect cancelled, as listeners have joined channel {} in guild {}", 
                    voiceChannel.getName(), guild.getName());
            return;
        }
        
        // Получаем TrackScheduler
        var trackScheduler = PlayerManager.getInstance()
            .getGuildMusicManager(guild)
            .getTrackScheduler();
        
        // Получаем текстовый канал и ID сообщения плеера
        TextChannel textChannel = trackScheduler.getTextChannel();
        String playerMessageId = trackScheduler.getPlayerMessageId();
        
        // Отправляем сообщение о том, что бот отключается
        if (textChannel != null) {
            textChannel.sendMessage("🔇 **Бот отключается от голосового канала из-за отсутствия слушателей.**")
                .queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
        }
        
        // Удаляем сообщение с плеером, если оно существует
        if (textChannel != null && playerMessageId != null) {
            textChannel.deleteMessageById(playerMessageId)
                .queue(null, error -> {/* Игнорируем ошибку, если сообщение уже удалено */});
        }
        
        // Останавливаем воспроизведение и очищаем очередь
        trackScheduler.stop();
        
        // Отключаемся от голосового канала
        guild.getAudioManager().closeAudioConnection();
        
        // Удаляем менеджер после остановки
        ru.discordj.bot.lavaplayer.PlayerManager.getInstance().removeGuildMusicManager(guild);
        
        // Log the event
        logger.info("Bot disconnected from voice channel {}, as it was left alone in guild {} for {} seconds", 
                voiceChannel.getName(), guild.getName(), DISCONNECT_DELAY_SECONDS);
    }
} 