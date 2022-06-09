package io.github.darkkronicle.proximitychat;

import com.google.gson.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class SettingsHandler {

    private static final SettingsHandler INSTANCE = new SettingsHandler();

    public final static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static SettingsHandler getInstance() {
        return INSTANCE;
    }

    @Getter @Setter private int distance = 30;

    public File getFile() {
        return new File("config/proximitychat.json");
    }

    public void save() {
        JsonObject obj = new JsonObject();
        obj.addProperty("distance", distance);
        try {
            write(obj, getFile());
        } catch (IOException e) {
            ProximityChat.LOGGER.log(Level.ERROR, "Couldn't save config!", e);
        }
    }

    public void load() {
        File file = getFile();
        if (!file.exists()) {
            try {
                // Just do defaultsfalse
                file.getParentFile().mkdirs();
                file.createNewFile();
                save();
            } catch (IOException e) {
                ProximityChat.LOGGER.log(Level.ERROR, "Couldn't save new file!", e);
            }
            return;
        }

        try {
            readJson(getFile());
        } catch (IOException e) {
            ProximityChat.LOGGER.log(Level.ERROR, "Could not load config!");
        }
    }

    public static String toString(JsonElement element) {
        return GSON.toJson(element);
    }

    public static JsonElement readJson(File file) throws IOException {
        return JsonParser.parseReader(GSON.newJsonReader(new StringReader(readFile(file))));
    }

    public void write(JsonElement element, File file) throws IOException {
        write(toString(element), file);
    }

    public static void write(String data, File file) throws IOException {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write(data);
        }
    }

    public static String readFile(File file) throws IOException {
        try (FileInputStream reader = new FileInputStream(file)) {
            return IOUtils.toString(reader, StandardCharsets.UTF_8);
        }
    }

}
