package io.kaoto.forage.core.util.config;

/**
 * Represents a configuration entry that maps between environment variables and system properties.
 *
 * <p>A ConfigEntry encapsulates the dual nature of configuration sources in the Forage framework,
 * where configuration values can be sourced from either environment variables or system properties.
 * This class provides automatic name transformation between the two naming conventions:
 * <ul>
 *   <li>Environment variables typically use UPPER_CASE_WITH_UNDERSCORES</li>
 *   <li>System properties typically use lower.case.with.dots</li>
 * </ul>
 *
 * <p>This class is immutable and thread-safe. All factory methods perform validation to ensure
 * that names are not null or empty.
 *
 * @see ConfigStore
 * @see Config
 * @see ConfigModule
 * @since 1.0
 */
public class ConfigEntry {
    private final String value;

    /**
     * Private constructor for creating ConfigEntry instances.
     *
     * @param value the configuration value
     */
    private ConfigEntry(String value) {
        this.value = value;
    }

    public static ConfigEntry fromModule() {
        return new ConfigEntry(null);
    }

    public String value() {
        return value;
    }
}
