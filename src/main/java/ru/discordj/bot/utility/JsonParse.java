package ru.discordj.bot.utility;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.discordj.bot.utility.pojo.Root;
import ru.discordj.bot.utility.pojo.RulesMessage;

import java.io.File;
import java.io.IOException;

public class JsonParse implements IJsonHandler {
    private static final String RULES_FILE = "json/rules.json";
    private static final String CONFIG_FILE = "json/config.json";
    private static final String DEFAULT_CONFIG = "{"
        + "\"token\": \"empty\","
        + "\"owner\": \"empty\","
        + "\"inviteLink\": \"empty\","
        + "\"roles\": ["
        + "  {"
        + "    \"channelId\": \"empty\","
        + "    \"roleId\": \"empty\","
        + "    \"emojiId\": \"empty\""
        + "  }"
        + "]"
        + "}";

    private final ObjectMapper mapper;
    private static JsonParse instance;

    private JsonParse() {
        this.mapper = new ObjectMapper();
        createConfigIfNotExists();
        createRulesIfNotExists();
    }

    public static synchronized JsonParse getInstance() {
        if (instance == null) {
            instance = new JsonParse();
        }
        return instance;
    }

    private void createRulesIfNotExists() {
        File rulesFile = new File(getRulesPath());
        if (!rulesFile.exists()) {
            try {
                rulesFile.getParentFile().mkdirs();
                RulesMessage defaultRules = new RulesMessage();
                defaultRules.setTitle("█▓▒░⡷⠂𝚃𝚑𝚎 𝚂𝚝𝚎𝚊𝚕𝚝𝚑 𝙳𝚞𝚍𝚎⠐⢾░▒▓█");
                defaultRules.setWelcomeField("WELCOME");
                defaultRules.setRulesField("️1️⃣ Все участники сервера имеют равные права...");
                defaultRules.setFooter("📩 requested by @{author} {date}");
                writeRules(defaultRules);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create rules.json", e);
            }
        }
    }

    private void createConfigIfNotExists() {
        File configFile = new File(getConfigPath());
        if (!configFile.exists()) {
            try {
                configFile.getParentFile().mkdirs();
                Root defaultConfig = mapper.readValue(DEFAULT_CONFIG, Root.class);
                write(defaultConfig);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create config.json", e);
            }
        }
    }

    @Override
    public RulesMessage readRules() {
        try {
            return mapper.readValue(new File(getRulesPath()), RulesMessage.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read rules.json", e);
        }
    }

    @Override
    public void writeRules(RulesMessage rules) {
        try {
            mapper.writerWithDefaultPrettyPrinter()
                .writeValue(new File(getRulesPath()), rules);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write rules.json", e);
        }
    }

    @Override
    public Root read() {
        try {
            return mapper.readValue(new File(getConfigPath()), Root.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read config.json", e);
        }
    }

    @Override
    public void write(Root root) {
        try {
            mapper.writerWithDefaultPrettyPrinter()
                .writeValue(new File(getConfigPath()), root);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write config.json", e);
        }
    }

    private String getRulesPath() {
        String jarPath = new File(getClass().getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath())
                .getParent();
        return jarPath + File.separator + RULES_FILE;
    }

    private String getConfigPath() {
        String jarPath = new File(getClass().getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath())
                .getParent();
        return jarPath + File.separator + CONFIG_FILE;
    }

} 