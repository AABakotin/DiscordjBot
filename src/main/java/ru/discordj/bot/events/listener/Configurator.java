package ru.discordj.bot.events.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.discordj.bot.config.utility.JsonHandler;
import ru.discordj.bot.config.utility.JsonParse;
import ru.discordj.bot.config.utility.pojo.Roles;
import ru.discordj.bot.config.utility.pojo.Root;
import ru.discordj.bot.informer.parser.Parser;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class Configurator extends ListenerAdapter {

    private final JsonHandler jsonHandler = JsonParse.getInstance();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        Root root = jsonHandler.read();
        List<Roles> rolesList = jsonHandler.read().getRoles();
        String[] command = event.getMessage().getContentRaw().split(" ");
        if (root.getOwner().equals("empty") || root.getOwner().isEmpty() || root.getOwner().equals(event.getAuthor().getId())) {
            switch (command[0]) {
                case "!read_conf":
                    if (jsonHandler.read().getOwner() == null || jsonHandler.read().getOwner().equals("empty")) {
                        event.getChannel().sendMessage("В базе нет ID администратора, введите !id.").queue();
                        break;
                    } else if (event.getMessage().getAuthor().getId().equals(jsonHandler.read().getOwner())) {
                        event.getChannel().sendMessageEmbeds(embedConfiguration()).queue();
                        break;
                    }
                case "!id":
                    String id = event.getMessage().getAuthor().getId();
                    if (jsonHandler.read().getOwner() == null || jsonHandler.read().getOwner().equals("empty")) {
                        root.setOwner(id);
                        jsonHandler.write(root);
                        event.getChannel().sendMessageEmbeds(embedConfiguration()).queue();
                        break;
                    }
                    event.getChannel().sendMessage("Уже установлен ID администратора: " + root.getOwner())
                            .queue(e -> event.getChannel().sendMessageEmbeds(embedConfiguration()).queue());
                    break;
                case "id_del":
                    if (event.getMessage().getAuthor().getId().equals(root.getOwner())) {
                        root.setOwner("empty");
                        jsonHandler.write(root);
                    }
                    event.getChannel().sendMessageEmbeds(embedConfiguration()).queue();
                    break;
                case "!roles":
                    try {
                        Roles role = new Roles(command[1], command[2], command[3]);
                        rolesList.add(role);
                        root.setRoles(rolesList);
                        jsonHandler.write(root);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        event.getChannel().sendMessage("Не верная команда! Вводить необходимо 3 значения через пробел. \n!roles id-канала id-роли id-емодзи.").queue();
                    }
                    event.getChannel().sendMessageEmbeds(embedConfiguration()).queue();
                    break;
                case "!token":
                    root.setToken(command[1]);
                    jsonHandler.write(root);
                    event.getChannel().sendMessageEmbeds(embedConfiguration()).queue();
                    break;
                case "!link":
                    root.setInvite_link(command[1]);
                    jsonHandler.write(root);
                    event.getChannel().sendMessageEmbeds(embedConfiguration()).queue();
                    break;
                case "!del_roles":
                    if (command[1].equals("all")) {
                        List<Roles> newRoles = new ArrayList<>();
                        root.setRoles(newRoles);
                        jsonHandler.write(root);
                        event.getChannel().sendMessageEmbeds(embedConfiguration()).queue();
                        break;
                    }
                    int index;
                    try {
                        index = Integer.parseInt(command[1]) - 1;
                    } catch (NumberFormatException e) {
                        event.getChannel().sendMessage("Не верные данные! Должно быть число после !del_channel 1 или all").queue();
                        return;
                    }
                    try {
                        rolesList.remove(index);
                        root.setRoles(rolesList);
                        jsonHandler.write(root);
                    } catch (IndexOutOfBoundsException e) {
                        event.getChannel().sendMessage("Не верное число! Всего правил: " + rolesList.size()).queue();
                    }
                    event.getChannel().sendMessageEmbeds(embedConfiguration()).queue();
                    break;
            }
        }
    }

    public MessageEmbed embedConfiguration() {
        EmbedBuilder builder = new EmbedBuilder()
                .setColor(Color.BLUE)
                .setTitle("█▓▒░⡷⠂Configuration⠐⢾░▒▓█")
                .addField("Token", jsonHandler.read().getToken(), false)
                .addField("Owner", jsonHandler.read().getOwner(), false)
                .addField("invite_link", jsonHandler.read().getInvite_link(), false);
        jsonHandler.read().getRoles().forEach(
                e -> builder.addField("\nChannel :" + e.getChannel() +
                                "\nRole: " + e.getRole() +
                                "\nEmoji: " + e.getEmoji(),
                        "", false));
        builder.setDescription("Убедитесь, что только ВЫ видите переписку с ботом!");
        builder.setFooter("Список команд: " +
                "\n!read_conf - показывает настройки бота, " +
                "\n!id - копирует ID админа автоматически, " +
                "\n!id_del - удаляет ID админа. Только админ может удалить себя из списка, " +
                "\n!roles - добавляет правило для авто-роли (id_канал id_роль id_емодзи), " +
                "\n!token - записывает токен (токен), " +
                "\n!link - ссылка приглашения в дискорд (URL), " +
                "\n!del_roles - удаляет правило авто-роли (число)");
        return builder.build();
    }

}


