package org.apache.camel.forage.core.util.config;

import java.util.Objects;

/**
 * Represents a configuration entry that maps between environment variables and system properties.
 *
 * <p>A ConfigEntry encapsulates the dual nature of configuration sources in the Camel Forage framework,
 * where configuration values can be sourced from either environment variables or system properties.
 * This class provides automatic name transformation between the two naming conventions:
 * <ul>
 *   <li>Environment variables typically use UPPER_CASE_WITH_UNDERSCORES</li>
 *   <li>System properties typically use lower.case.with.dots</li>
 * </ul>
 *
 * <p>The {@link ConfigStore} uses ConfigEntry instances to determine where to look for configuration
 * values, checking environment variables first, then system properties as fallback.
 *
 * <p><strong>Usage Examples:</strong>
 * <pre>{@code
 * // Create from environment variable - converts MY_API_KEY to my.api.key
 * ConfigEntry entry1 = ConfigEntry.fromEnv("MY_API_KEY");
 *
 * // Create from property name - converts my.timeout.value to MY.TIMEOUT.VALUE
 * ConfigEntry entry2 = ConfigEntry.fromProperty("my.timeout.value");
 *
 * // Direct construction with custom names
 * ConfigEntry entry3 = new ConfigEntry("CUSTOM_ENV", "custom.property");
 * }</pre>
 *
 * <p><strong>Name Transformation Rules:</strong>
 * <ul>
 *   <li>{@link #fromEnv(String)}: Converts underscores to dots and transforms to lowercase</li>
 *   <li>{@link #fromProperty(String)}: Converts underscores to dots and transforms to uppercase</li>
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
    private final String envName;
    private final String propertyName;

    /**
     * Private constructor for creating ConfigEntry instances.
     *
     * @param envName the environment variable name
     * @param propertyName the system property name
     */
    private ConfigEntry(String envName, String propertyName) {
        this.envName = envName;
        this.propertyName = propertyName;
    }

    /**
     * Creates a ConfigEntry from an environment variable name.
     *
     * <p>This factory method automatically transforms the environment variable name to a corresponding
     * system property name by replacing underscores with dots and converting to lowercase.
     *
     * <p><strong>Transformation Examples:</strong>
     * <ul>
     *   <li>{@code "MY_API_KEY"} → {@code "my.api.key"}</li>
     *   <li>{@code "DATABASE_URL"} → {@code "database.url"}</li>
     *   <li>{@code "CONFIG"} → {@code "config"}</li>
     * </ul>
     *
     * @param envName the environment variable name, must not be null or empty
     * @return a new ConfigEntry with the environment name and transformed property name
     * @throws NullPointerException if envName is null
     * @throws IllegalArgumentException if envName is empty
     */
    public static ConfigEntry fromEnv(String envName) {
        Objects.requireNonNull(envName, "envName");

        if (envName.isEmpty()) {
            throw new IllegalArgumentException("Environment name cannot be empty");
        }

        return new ConfigEntry(envName, envName.replace("_", ".").toLowerCase());
    }

    public static ConfigEntry fromModule(ConfigModule module, String envName) {
        Objects.requireNonNull(module, "module");

        if (envName == null || envName.isEmpty()) {
            envName = envName.replace(".", "_").toUpperCase();
        }

        return new ConfigEntry(envName, module.name());
    }

    /**
     * Creates a ConfigEntry from a system property name.
     *
     * <p>This factory method automatically transforms the system property name to a corresponding
     * environment variable name by replacing underscores with dots and converting to uppercase.
     *
     * <p><strong>Transformation Examples:</strong>
     * <ul>
     *   <li>{@code "my.api.key"} → {@code "MY.API.KEY"}</li>
     *   <li>{@code "database_url"} → {@code "DATABASE.URL"}</li>
     *   <li>{@code "timeout"} → {@code "TIMEOUT"}</li>
     * </ul>
     *
     * @param propertyName the system property name, must not be null or empty
     * @return a new ConfigEntry with the property name and transformed environment name
     * @throws NullPointerException if propertyName is null
     * @throws IllegalArgumentException if propertyName is empty
     */
    public static ConfigEntry fromProperty(String propertyName) {
        Objects.requireNonNull(propertyName, "propertyName");

        if (propertyName.isEmpty()) {
            throw new IllegalArgumentException("Property name cannot be empty");
        }

        return new ConfigEntry(propertyName, propertyName.replace("_", ".").toUpperCase());
    }

    /**
     * Returns the environment variable name for this configuration entry.
     *
     * <p>This is the name that will be used when looking up values from environment variables
     * via {@link System#getenv(String)}.
     *
     * @return the environment variable name, never null
     */
    public String envName() {
        return envName;
    }

    /**
     * Returns the system property name for this configuration entry.
     *
     * <p>This is the name that will be used when looking up values from system properties
     * via {@link System#getProperty(String)} as a fallback when the environment variable
     * is not found.
     *
     * @return the system property name, never null
     */
    public String propertyName() {
        return propertyName;
    }
}
