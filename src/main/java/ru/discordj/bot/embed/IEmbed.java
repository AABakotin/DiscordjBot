package ru.discordj.bot.embed;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public interface IEmbed {

     MessageCreateData playListEmbed(TextChannel textChannel);


     MessageEmbed embedBay(String imageServer, String author);

     MessageEmbed  embedWelcome(String imageServer, String author);

     MessageEmbed infoUser (SlashCommandInteractionEvent event);


}
