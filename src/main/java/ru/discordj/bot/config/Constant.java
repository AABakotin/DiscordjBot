package ru.discordj.bot.config;


import io.github.cdimascio.dotenv.Dotenv;
import org.jetbrains.annotations.NotNull;
import ru.discordj.bot.config.utility.JsonHandler;
import ru.discordj.bot.config.utility.JsonParse;


public final class Constant {
    @NotNull
    private static final Dotenv dotenv =
            Dotenv.configure()
                    .filename(".env")
                    .ignoreIfMalformed()
                    .load();

    public static JsonHandler jsonHandler = JsonParse.getInstance();

    public static  String NON_AVATAR_URL = "http://i.servimg.com/u/f33/17/73/99/79/no_ava10.png";
    public static  String TOKEN_FROM_FILE_PROPERTIES = jsonHandler.read().getToken();
    public static  String INVITATION_LINK = jsonHandler.read().getInvite_link();

    public static final String TEST_CHANNEL = "1300539782874529802";


    public static final byte INFO_RESPONSE = 0x49;
    public static final byte[] A2S_INFO = {
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x54, 0x53, (byte) 0x6F, (byte) 0x75, (byte) 0x72,
            (byte) 0x63, (byte) 0x65, (byte) 0x20, (byte) 0x45, (byte) 0x6E, (byte) 0x67, (byte) 0x69, (byte) 0x6E,
            (byte) 0x65, (byte) 0x20, (byte) 0x51, (byte) 0x75, (byte) 0x65, (byte) 0x72, (byte) 0x79, (byte) 0x00,
            (byte) 0x0A, (byte) 0x08, (byte) 0x5E, (byte) 0xEA
    };


    private Constant() {
        throw new IllegalStateException("Constant class");
    }

}


