package ru.discordj.bot.config;


public final class Constant {

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


