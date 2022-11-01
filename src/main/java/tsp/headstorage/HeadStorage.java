package tsp.headstorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HeadStorage {

    private final Logger logger = LoggerFactory.getLogger("HeadStorage");
    private final File container;

    public HeadStorage(String[] args) {
        this.container = args.length > 0 ? new File(args[0]) : new File("./storage");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void update() {
        for (Category category : Category.VALUES) {
            logger.info("Fetching: " + category.getName());
            try {
                RequestUtils.fetchCategory(category).ifPresentOrElse(response -> {
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
                        writer.write(response);
                        logger.info("Updated file category: " + category.name());
                    } catch (IOException ex) {
                        logger.error("Could not write to file: "+ file.getName());
                    }
                }, () -> logger.error("No heads returned for: " + category.name()));
            } catch (IOException ex) {
                logger.error("Failed to fetch heads for: " + category.name(), ex);
            }
        }
    }

}
