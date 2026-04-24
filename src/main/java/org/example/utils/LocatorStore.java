package org.example.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocatorStore {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final ConcurrentHashMap<String, JsonNode> CACHE = new ConcurrentHashMap<>();

    private LocatorStore() {}

    public static String get(String page, String key) {
        JsonNode node = loadPage(page).get(key);
        if (node == null) {
            throw new CustomExceptions.ConfigurationException(
                    "Locator key '" + key + "' not found in locators/" + page + ".json");
        }
        return node.asText();
    }

    public static String get(String page, String key, Map<String, String> params) {
        String selector = get(page, key);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            if (selector.contains(placeholder)) {
                selector = selector.replace(placeholder, entry.getValue());
            } else {
                throw new CustomExceptions.ConfigurationException(
                        "Placeholder '" + placeholder + "' not found in selector: " + selector);
            }
        }
        return selector;
    }

    public static String get(String page, String key, String paramName, String paramValue) {
        return get(page, key, Map.of(paramName, paramValue));
    }

    public static String toLocatorToken(String productName) {
        return productName.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }

    private static JsonNode loadPage(String page) {
        return CACHE.computeIfAbsent(page, p -> {
            String path = "locators/" + p + ".json";
            try (InputStream is = LocatorStore.class.getClassLoader().getResourceAsStream(path)) {
                if (is == null) {
                    throw new CustomExceptions.ConfigurationException(
                            "Locator file not found on classpath: " + path);
                }
                return MAPPER.readTree(is);
            } catch (CustomExceptions.ConfigurationException e) {
                throw e;
            } catch (Exception e) {
                throw new CustomExceptions.ConfigurationException(
                        "Failed to load locator file: " + path + " — " + e.getMessage());
            }
        });
    }
}
