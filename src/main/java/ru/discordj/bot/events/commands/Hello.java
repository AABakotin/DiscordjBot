//package ru.discordj.bot.Events.Commands;
//
//import java.util.Objects;
//
//public class Hello implements ICommand {
//    private static final Logger logger = LoggerFactory.getLogger(ru.discordj.bot.Events.Commands.ReadyEventListener.class);
//
//    public Hello() {
//    }
//
//    public void handle(CommandContext ctx) {
//        ctx.getChannel().sendTyping().queue();
//        ctx.getMessage().delete().queue();
//        String userName = ((Member) Objects.requireNonNull(ctx.getMember())).getUser().getName();
//        ctx.getChannel().sendMessage("Приветики " + userName).queue();
//        ctx.getChannel().sendMessage((CharSequence)Objects.requireNonNull(ctx.getAuthor().getAvatarUrl())).queue();
//        logger.info("Hi" + userName);
//    }
//
//    public String getName() {
//        return "hello";
//    }
//}