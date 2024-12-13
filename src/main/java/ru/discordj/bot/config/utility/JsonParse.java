package ru.discordj.bot.config.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.discordj.bot.config.utility.pojo.Root;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class JsonParse implements JsonHandler {

    private final ObjectMapper mapper;
    private static JsonParse instance;
    private static final String CONFIG_FILE = "config.json";
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

    private JsonParse() {
        this.mapper = new ObjectMapper();
        createConfigIfNotExists();
    }

    public static synchronized JsonParse getInstance() {
        if (instance == null) {
            instance = new JsonParse();
        }
        return instance;
    }

    private void createConfigIfNotExists() {
        File configFile = new File(getConfigPath());
        if (!configFile.exists()) {
            try {
                // Пробуем сначала прочитать из ресурсов
                try (InputStream is = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
                    if (is != null) {
                        Root config = mapper.readValue(is, Root.class);
                        write(config);
                        return;
                    }
                }
                // Если в ресурсах нет, создаем с дефолтными значениями
                configFile.getParentFile().mkdirs();
                Root defaultConfig = mapper.readValue(DEFAULT_CONFIG, Root.class);
                write(defaultConfig);
            } catch (IOException e) {
                throw new RuntimeException("Не удалось создать файл конфигурации", e);
            }
        }
    }

    private String getConfigPath() {
        String jarPath = new File(getClass().getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath())
                .getParent();
        return jarPath + File.separator + CONFIG_FILE;
    }

    @Override
    public Root read() {
        try {
            return mapper.readValue(new File(getConfigPath()), Root.class);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении конфигурации: " + e.getMessage(), e);
        }
    }

    @Override
    public void write(Root root) {
        try {
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(getConfigPath()), root);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при записи конфигурации: " + e.getMessage(), e);
        }
    }
}
