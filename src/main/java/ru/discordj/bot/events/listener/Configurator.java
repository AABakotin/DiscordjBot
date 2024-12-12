package ru.discordj.bot.events.listener;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.discordj.bot.config.utility.JsonHandler;
import ru.discordj.bot.config.utility.JsonParse;
import ru.discordj.bot.config.utility.pojo.Roles;
import ru.discordj.bot.config.utility.pojo.Root;
import ru.discordj.bot.embed.EmbedCreation;

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
                        event.getChannel().sendMessageEmbeds(EmbedCreation.get().embedConfiguration()).queue();
                        break;
                    }
                case "!id":
                    String id = event.getMessage().getAuthor().getId();
                    if (jsonHandler.read().getOwner() == null || jsonHandler.read().getOwner().equals("empty")) {
                        root.setOwner(id);
                        jsonHandler.write(root);
                        event.getChannel().sendMessageEmbeds(EmbedCreation.get().embedConfiguration()).queue();
                        break;
                    }
                    event.getChannel().sendMessage("Уже установлен ID администратора: " + root.getOwner())
                            .queue(e -> event.getChannel().sendMessageEmbeds(EmbedCreation.get().embedConfiguration()).queue());
                    break;
                case "!id_del":
                    if (event.getMessage().getAuthor().getId().equals(root.getOwner())) {
                        root.setOwner("empty");
                        jsonHandler.write(root);
                    }
                    event.getChannel().sendMessageEmbeds(EmbedCreation.get().embedConfiguration()).queue();
                    break;
                case "!role":
                    try {
                        Roles role = new Roles(command[1], command[2], command[3]);
                        rolesList.add(role);
                        root.setRoles(rolesList);
                        jsonHandler.write(root);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        event.getChannel().sendMessage("Не верная команда! Вводить необходимо 3 значения через пробел. \n!roles id-канала id-роли id-емодзи.").queue();
                    }
                    event.getChannel().sendMessageEmbeds(EmbedCreation.get().embedConfiguration()).queue();
                    break;
                case "!token":
                    root.setToken(command[1]);
                    jsonHandler.write(root);
                    event.getChannel().sendMessageEmbeds(EmbedCreation.get().embedConfiguration()).queue();
                    break;
                case "!link":
                    root.setInvite_link(command[1]);
                    jsonHandler.write(root);
                    event.getChannel().sendMessageEmbeds(EmbedCreation.get().embedConfiguration()).queue();
                    break;
                case "!del_role":
                    if (command[1].equals("all")) {
                        List<Roles> newRoles = new ArrayList<>();
                        root.setRoles(newRoles);
                        jsonHandler.write(root);
                        event.getChannel().sendMessageEmbeds(EmbedCreation.get().embedConfiguration()).queue();
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
                    event.getChannel().sendMessageEmbeds(EmbedCreation.get().embedConfiguration()).queue();
                    break;
            }
        }
    }

}


