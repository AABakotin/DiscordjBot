package ru.discordj.bot.config;


import io.github.cdimascio.dotenv.Dotenv;
import org.jetbrains.annotations.NotNull;

final public class Constant {
    @NotNull
    private static final Dotenv dotenv =
            Dotenv.configure()
            .filename("properties.env")
            .load();


    public static final String OWNER = dotenv.get("OWNER");
    public static final String ADMIN_CHANNEL = dotenv.get("ADMIN_CHANNEL");
    public static final String GUEST_CHANNEL = dotenv.get("GUEST_CHANNEL");
    public static final String EMOJI_ACCESS = dotenv.get("EMOJI_ACCESS");
    public static final String ROLE_ACCESS = dotenv.get("ROLE_ACCESS");
    public static final String EMOJI_JAVA = dotenv.get("EMOJI_JAVA");
    public static final String ROLE_JAVA = dotenv.get("ROLE_JAVA");
    public static final String NON_AVATAR_URL = dotenv.get("NON_AVATAR_URL");
    public static final String TOKEN_FROM_FILE_PROPERTIES = dotenv.get("TOKEN_FROM_FILE");
    public static final String INVITATION_LINK = dotenv.get("INVITATION_LINK");


    public static final byte INFO_RESPONSE = 0x49;
    public static final byte[] A2S_INFO = {
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x54, 0x53, (byte) 0x6F, (byte) 0x75, (byte) 0x72,
            (byte) 0x63, (byte) 0x65, (byte) 0x20, (byte) 0x45, (byte) 0x6E, (byte) 0x67, (byte) 0x69, (byte) 0x6E,
            (byte) 0x65, (byte) 0x20, (byte) 0x51, (byte) 0x75, (byte) 0x65, (byte) 0x72, (byte) 0x79, (byte) 0x00,
            (byte) 0x0A, (byte) 0x08, (byte) 0x5E, (byte) 0xEA
    };

    private Constant() {
    }
}


