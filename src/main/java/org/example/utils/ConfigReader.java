package org.example.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final Properties properties = new Properties();

    static {
        loadProperties("config.properties");
        String env = System.getProperty("ENV", System.getenv("ENV") != null ? System.getenv("ENV") : "local");
        if ("ci".equalsIgnoreCase(env)) {
            loadProperties("config/config-ci.properties");
        } else {
            loadProperties("config/config-local.properties");
        }
        // System properties override everything
        properties.putAll(System.getProperties());
    }

    private static void loadProperties(String fileName) {
        try (InputStream is = ConfigReader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (is != null) {
                properties.load(is);
            }
        } catch (IOException e) {
            // Non-fatal: optional override files may not exist
        }
    }

    public static String getConfig(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new CustomExceptions.ConfigurationException("Missing required config key: " + key);
        }
        return value.trim();
    }

    public static String getConfig(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue).trim();
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(getConfig(key));
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(getConfig(key, String.valueOf(defaultValue)));
    }

    public static int getInt(String key) {
        return Integer.parseInt(getConfig(key));
    }

    public static int getInt(String key, int defaultValue) {
        return Integer.parseInt(getConfig(key, String.valueOf(defaultValue)));
    }

    public static String getBaseUrl() {
        return getConfig("base.url");
    }

    public static String getBrowserType() {
        return getConfig("browser.type", "chromium");
    }

    public static boolean isHeadless() {
        return getBoolean("browser.headless", false);
    }

    public static int getTimeout() {
        return getInt("browser.timeout", 10000);
    }
}
