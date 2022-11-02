package tsp.headstorage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

public class Utils {

    public static Optional<String> fetchCategory(Category category) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(category.getUrl()).openConnection();
        connection.setConnectTimeout(0); // Infinite
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "HeadStorage/1.0.0");
        connection.setRequestProperty("Accept", "application/json");

        if (connection.getResponseCode() == 200) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                return Optional.of(builder.toString());
            }
        }

        connection.disconnect();
        return Optional.empty();
    }

}