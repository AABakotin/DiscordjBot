package ru.discordj.bot.embed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.discordj.bot.utility.IJsonHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import ru.discordj.bot.utility.pojo.RulesMessage;
import lombok.extern.slf4j.Slf4j;

import java.awt.Color;
import java.util.Map;

@Component
@Slf4j
public class WelcomeEmbed extends BaseEmbed {
    private static final String WELCOME_TITLE = "Добро пожаловать на сервер!";
    private static final String LEAVE_TITLE = "Спасибо что были с нами!";
    private static final String RULES_TITLE = "Правила сервера";
    
    private static final Color WELCOME_COLOR = Color.GREEN;
    private static final Color LEAVE_COLOR = Color.RED;
    private static final Color RULES_COLOR = Color.BLUE;
    
    private static final Map<String, String> EMOJI = Map.ofEntries(
        Map.entry("WELCOME", "👋"),
        Map.entry("LEAVE", "😢"),
        Map.entry("RULES", "📜")
    );

    @Autowired
    public WelcomeEmbed(IJsonHandler jsonHandler) {
        super(jsonHandler);
    }

    /**
     * Создает приветственный embed при входе на сервер
     */
    public MessageEmbed embedWelcomeGuild(String avatarUrl, String username) {
        return createDefaultBuilder()
            .setColor(WELCOME_COLOR)
            .setTitle(EMOJI.get("WELCOME") + " " + WELCOME_TITLE)
            .setThumbnail(avatarUrl)
            .setDescription(formatWelcomeMessage(username))
            .addField("Полезные ссылки:", createUsefulLinks(), false)
            .build();
    }

    /**
     * Создает прощальный embed при выходе с сервера
     */
    public MessageEmbed embedLeaveGuild(String avatarUrl, String username) {
        return createDefaultBuilder()
            .setColor(LEAVE_COLOR)
            .setTitle(EMOJI.get("LEAVE") + " " + LEAVE_TITLE)
            .setThumbnail(avatarUrl)
            .setDescription(formatLeaveMessage(username))
            .build();
    }

    /**
     * Создает embed с правилами сервера
     */
    public MessageEmbed embedRules() {
        RulesMessage rules = jsonHandler.readRules();
        if (rules == null || rules.getRulesField() == null || rules.getRulesField().isEmpty()) {
            return createErrorEmbed();
        }

        EmbedBuilder builder = createDefaultBuilder()
            .setColor(RULES_COLOR)
            .setTitle(EMOJI.get("RULES") + " " + RULES_TITLE)
            .setDescription(rules.getRulesField());

        return builder.build();
    }

    private String formatWelcomeMessage(String username) {
        return String.format("Привет, %s!\n\n" +
            "Рады видеть тебя на нашем сервере!\n" +
            "Ознакомься с правилами и хорошего времяпрепровождения!", 
            username);
    }

    private String formatLeaveMessage(String username) {
        return String.format("Прощай, %s!\n\n" +
            "Надеемся, ты ещё вернёшься к нам!", 
            username);
    }

    private String createUsefulLinks() {
        String inviteLink = jsonHandler.read().getInviteLink();
        // Если ссылка не установлена, используем значение по умолчанию
        if (inviteLink == null || inviteLink.equals("empty")) {
            inviteLink = "https://discord.gg/invite";
            log.warn("Invite link not set in config.json, using default");
        }

        return String.format("[Discord](%s)\n" +
               "[GitHub](https://github.com/AABakotin/DiscordjBot)\n" +
               "[Website](https://example.com)", 
               inviteLink);
    }

    private MessageEmbed createErrorEmbed() {
        return createDefaultBuilder()
            .setColor(Color.RED)
            .setTitle("⚠️ Ошибка")
            .setDescription("Правила не найдены или файл правил пуст")
            .build();
    }
}