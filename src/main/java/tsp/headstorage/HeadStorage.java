package tsp.headstorage;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HeadStorage {

    private final Logger logger = LoggerFactory.getLogger("HeadStorage");
    private final File container;
    private final File idContainer;
    private final Map<String, Integer> ids = new HashMap<>();
    private int lastId;

    public HeadStorage(String[] args) {
        this.container = args.length > 0 ? new File(args[0]) : new File("./storage");
        this.idContainer = new File("./ids.json");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void update() {
        if (!container.exists()) {
            container.mkdir();
        }

        loadIds();
        for (Category category : Category.VALUES) {
            logger.info("Fetching: " + category.getName());
            try {
                Utils.fetchCategory(category).ifPresentOrElse(response -> {
                    File file = new File(container, category.getName() + ".json");
                    if (file.exists()) {
                        if (file.delete()) {
                            logger.info("Deleted previous category file: " + file.getName());
                        } else {
                            logger.warn("Could not delete previous category file: " + file.getName());
                        }
                    }

                    try {
                        file.createNewFile();
                    } catch (IOException ex) {
                        logger.error("Could not create new file for category: " + category.name(), ex);
                    }

                    try (FileWriter writer = new FileWriter(file)) {
                        writer.write(injectIds(response));
                        logger.info("Updated file category: " + category.name());
                    } catch (IOException ex) {
                        logger.error("Could not write to file: " + file.getName());
                    }
                }, () -> logger.error("No heads returned for: " + category.name()));
            } catch (IOException ex) {
                logger.error("Failed to fetch heads for: " + category.name(), ex);
            }
        }

        saveIds();
        logger.info("Done!");
    }

    private void loadIds() {
        if (!ids.isEmpty()) {
            ids.clear();
        }

        logger.debug("Loading ids...");
        try (FileReader reader = new FileReader(idContainer)) {
            JsonObject main = JsonParser.parseReader(reader).getAsJsonObject();
            lastId = main.get("last_id").getAsInt();

            JsonArray array = main.get("ids").getAsJsonArray();
            if (array.isEmpty()) {
                return;
            }

            int count = 0;
            for (JsonElement entry : array) {
                JsonObject obj = entry.getAsJsonObject();
                ids.put(obj.get("texture").getAsString(), obj.get("id").getAsInt());
                count++;
            }

            logger.debug("Loaded " + count + " ids!");
        } catch (IOException ex) {
            logger.error("Failed to read bytes from id container!", ex);
        }
    }

    private void saveIds() {
        logger.debug("Saving ids...");
        JsonObject main = new JsonObject();
        main.addProperty("last_id", lastId);

        JsonArray array = new JsonArray();
        for (Map.Entry<String, Integer> entry : ids.entrySet()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("texture", entry.getKey());
            obj.addProperty("id", entry.getValue());
            array.add(obj);
        }
        main.add("ids", array);

        try (FileWriter writer = new FileWriter(idContainer)) {
            writer.write(main.toString());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private int getIdOrAdd(String texture) {
        String st = texture.substring(0, 7);

        logger.trace("Retrieving id for: " + st);
        int id = this.ids.getOrDefault(texture, -1);
        if (id == -1) {
            id = (lastId = lastId + 1);
            this.ids.put(texture, id);
            logger.trace("No id present for '" + st + "' | Assigned new id: " + id);
        }

        return id;
    }

    private String injectIds(String json) {
        logger.debug("Injecting ids...");
        JsonArray result = new JsonArray();

        JsonArray main = JsonParser.parseString(json).getAsJsonArray();
        for (JsonElement entry : main) {
            JsonObject obj = entry.getAsJsonObject();
            obj.addProperty("id", getIdOrAdd(obj.get("value").getAsString()));
            result.add(obj);
        }

        return result.toString();
    }

}
