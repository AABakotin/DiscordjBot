package ru.discordj.bot.config.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.discordj.bot.config.utility.pojo.Root;

import java.io.File;
import java.io.IOException;

public class JsonParse implements JsonHandler {


    private final ObjectMapper mapper;
    private  static JsonParse instance;

    private JsonParse() {
        this.mapper = new ObjectMapper();
    }

    public static JsonParse getInstance() {
       if (instance == null) return new JsonParse();
       return instance;
    }



    @Override
    public Root read() {

        try {
            return mapper.readValue(new File("src/main/resources/.json"), Root.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(Root root) {
        try {
            mapper.writeValue(new File("src/main/resources/.json"), root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
