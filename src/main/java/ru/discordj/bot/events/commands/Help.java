//package ru.discordj.bot.events.commands;
//
//import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
//import net.dv8tion.jda.api.interactions.commands.OptionType;
//import net.dv8tion.jda.api.interactions.commands.build.OptionData;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import ru.discordj.bot.events.ICommand;
//
//import java.awt.*;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//public class Help implements ICommand {
//    private static final Logger logger = LoggerFactory.getLogger(Help.class);
//
//    public String getName() {
//        return "help";
//    }
//
//    @Override
//    public String getDescription() {
//        return "helper";
//    }
//
//    @Override
//    public List<OptionData> getOptions() {
//        List<OptionData> dataList = new ArrayList<>();
//        dataList.add(new OptionData(
//                OptionType.STRING, "help", "Helper", false));
//        return dataList;
//    }
//
//    @Override
//    public void execute(SlashCommandInteractionEvent event) {
//        List<String> args = event.getArgs();
//        if (args.isEmpty()) {
//            StringBuilder stringBuilder = new StringBuilder();
//            this.manager.getCommands().stream().map(ICommand::getName).forEach((it) -> {
//                stringBuilder.append(ConfigBot.get("PREFIX")).append(it).append("\n");
//            });
//            this.cmd = stringBuilder.toString();
//        }
//
//        event.getChannel().sendTyping().queue();
//        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//        Date date = new Date();
//        event.getMessage().delete().queue();
//        EmbedBuilder builder = (new EmbedBuilder()).setColor(Color.RED).setTitle("Helper").addField("Commands for TSDBot: ", this.cmd, false).setFooter("requested by @" + event.getAuthor().getName() + " " + formatter.format(date), event.getGuild().getIconUrl());
//        ((PrivateChannel)event.getAuthor().openPrivateChannel().complete()).sendMessageEmbeds(builder.build(), new MessageEmbed[0]).queue();
//        logger.info("Helper required by " + event.getAuthor().getName());
//    }
//}
