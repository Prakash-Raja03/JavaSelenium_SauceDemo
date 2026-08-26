package com.prakash.automation.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads configuration from src/test/resources/config/config.properties.
 * Any property can be overridden at runtime via a JVM system property, e.g.:
 *      mvn test -Dbrowser=firefox -Dheadless=true
 *
 * Loaded once (static block) and reused — a lightweight singleton pattern.
 */
public final class ConfigReader {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config/config.properties")) {
            if (input == null) {
                throw new RuntimeException("config.properties not found on classpath!");
            }
            PROPERTIES.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    private ConfigReader() {
        // Utility class — prevent instantiation
    }

    /**
     * Returns a config value. A matching JVM -D system property takes precedence,
     * which is how CI pipelines override values (browser, url, headless, etc.).
     */
    public static String get(String key) {
        return System.getProperty(key, PROPERTIES.getProperty(key));
    }

    public static String get(String key, String defaultValue) {
        return System.getProperty(key, PROPERTIES.getProperty(key, defaultValue));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }
}
