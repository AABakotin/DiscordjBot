package ru.discordj.bot.config;


import io.github.cdimascio.dotenv.Dotenv;
import org.jetbrains.annotations.NotNull;

final public class Constant {
    @NotNull
    private static final Dotenv dotenv =
            Dotenv.configure()
            .filename("properties.env")
            .load();

    public static final String GUEST_CHANNEL = dotenv.get("GUEST_CHANNEL");
    public static final String EMOJI_ACCESS = dotenv.get("EMOJI_ACCESS");
    public static final String ROLE_ACCESS = dotenv.get("ROLE_ACCESS");
    public static final String EMOJI_JAVA = dotenv.get("EMOJI_JAVA");
    public static final String ROLE_JAVA = dotenv.get("ROLE_JAVA");
    public static final String NON_AVATAR_URL = dotenv.get("NON_AVATAR_URL");
    public static final String TOKEN_FROM_FILE_PROPERTIES = dotenv.get("TOKEN_FROM_FILE");
    public static final String INVITATION_LINK = dotenv.get("INVITATION_LINK");

    private Constant() {
    }
}


