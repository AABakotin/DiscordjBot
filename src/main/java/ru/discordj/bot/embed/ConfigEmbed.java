package ru.discordj.bot.embed;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.EmbedBuilder;
import java.awt.Color;

public class ConfigEmbed extends BaseEmbed {
    public MessageEmbed embedConfiguration() {
        EmbedBuilder builder = new EmbedBuilder()
            .setColor(Color.BLUE)
            .setTitle("█▓▒░⡷⠂Configuration⠐⢾░▒▓█")
            .addField("Owner", jsonHandler.read().getOwner(), false)
            .addField("invite_link", jsonHandler.read().getInviteLink(), false);
        
        jsonHandler.read().getRoles().forEach(
            e -> builder.addField("\nChannel :" + e.getChannelId() +
                "\nRole: " + e.getRoleId() +
                "\nEmoji: " + e.getEmojiId(),
                "", false));
        
        builder.setDescription("Убедитесь, что только ВЫ видите переписку с ботом!");
        builder.setFooter("Список команд: " +
            "\n!read_conf - показывает настройки бота, " +
            "\n!id - копирует ID админа автоматически, " +
            "\n!id_del - удаляет ID админа. Только админ может удалить себя из списка, " +
            "\n!role - добавляет правило для авто-роли (id_канал id_роль id_емодзи), " +
            "\n!link - ссылка приглашения в дискорд (URL), " +
            "\n!del_role - удаляет правило авто-роли (число)");
        return builder.build();
    }
} 