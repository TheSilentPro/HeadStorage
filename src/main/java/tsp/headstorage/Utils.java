package tsp.headstorage;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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

    public static String compress(String str) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(str.getBytes(StandardCharsets.UTF_8));
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return out.toString(StandardCharsets.UTF_8);
    }

    public static Optional<String> decompress(byte[] compressed) {
        StringBuilder outStr = new StringBuilder();
        try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressed)); BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gis, StandardCharsets.UTF_8))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                outStr.append(line);
            }

            return Optional.of(outStr.toString());
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return Optional.empty();
    }

}