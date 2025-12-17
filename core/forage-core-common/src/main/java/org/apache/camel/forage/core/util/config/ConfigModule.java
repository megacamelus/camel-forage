package org.apache.camel.forage.core.util.config;

import java.util.Objects;

/**
 * Represents a configuration module that associates a configuration class with a specific configuration parameter.
 *
 * <p>A ConfigModule serves as a unique identifier for a specific configuration parameter within a module.
 * It combines the configuration class type with a parameter name to create a distinct key that can be
 * used by the {@link ConfigStore} to store and retrieve configuration values.
 *
 * <p>This record is typically used as a static constant within configuration classes to define
 * individual configuration parameters. Each ConfigModule represents one configurable aspect of a module.
 *
 * <p><strong>Usage Example:</strong>
 * <pre>{@code
 * public class DatabaseConfig implements Config {
 *     private static final ConfigModule CONNECTION_URL = ConfigModule.of(DatabaseConfig.class, "connection-url");
 *     private static final ConfigModule MAX_POOL_SIZE = ConfigModule.of(DatabaseConfig.class, "max-pool-size");
 *
 *     public DatabaseConfig() {
 *         ConfigStore.getInstance().add(CONNECTION_URL, ConfigEntry.fromEnv("DB_CONNECTION_URL"));
 *         ConfigStore.getInstance().add(MAX_POOL_SIZE, ConfigEntry.fromEnv("DB_MAX_POOL_SIZE"));
 *     }
 *
 *     public String connectionUrl() {
 *         return ConfigStore.getInstance().get(CONNECTION_URL)
 *                 .orElseThrow(() -> new MissingConfigException("Missing database connection URL"));
 *     }
 * }
 * }</pre>
 *
 * <p>The combination of configuration class and parameter name ensures that configuration parameters
 * are unique across the entire application, even if different modules use similar parameter names.
 *
 * <p>This record is immutable and thread-safe.
 *
 * @param config the configuration class that owns this parameter, must not be null
 * @param name the name of the configuration parameter within the module, must not be null
 * @see ConfigStore
 * @see Config
 * @see ConfigEntry
 * @since 1.0
 */
public class ConfigModule {

    private final Class<? extends Config> config;
    private final String name;
    private final String prefix;
    private final String description;
    private final String label;
    private final String defaultValue;
    private final String type;
    private final boolean required;
    private final ConfigTag configTag;

    public ConfigModule(Class<? extends Config> config, String name, String prefix) {
        this.config = config;
        this.name = name;
        this.prefix = prefix;
        this.description = null;
        this.label = null;
        this.defaultValue = null;
        this.type = null;
        this.required = false;
        this.configTag = null;
    }

    public ConfigModule(
            Class<? extends Config> config,
            String name,
            String prefix,
            String description,
            String label,
            String defaultValue,
            String type,
            boolean required,
            ConfigTag configTag) {
        this.config = config;
        this.name = name;
        this.prefix = prefix;
        this.description = description;
        this.label = label;
        this.defaultValue = defaultValue;
        this.type = type;
        this.required = required;
        this.configTag = configTag;
    }

    /**
     * Creates a new ConfigModule with the specified configuration class and parameter name.
     *
     * <p>This factory method provides a convenient way to create ConfigModule instances,
     * making the code more readable when defining configuration parameters as static constants.
     *
     * @param config the configuration class that owns this parameter, must not be null
     * @param name the name of the configuration parameter within the module, must not be null
     * @return a new ConfigModule instance
     * @throws NullPointerException if config or name is null
     */
    public static ConfigModule of(Class<? extends Config> config, String name) {
        return new ConfigModule(config, name, null);
    }

    public static ConfigModule of(
            Class<? extends Config> config,
            String name,
            String description,
            String label,
            String defaultValue,
            String type,
            boolean required,
            ConfigTag configTag) {
        return new ConfigModule(config, name, null, description, label, defaultValue, type, required, configTag);
    }

    /**
     * A configuration module may be prefixed, so that there can be multiple configurations for it.
     * This is useful when dealing with multimodel setups, where you might have different configurations
     * for the same module (i.e.: "agent1.model.context-window=1024", "agent2.model.context-window=2048", etc).
     *
     * @param prefix
     * @return
     */
    public ConfigModule asNamed(String prefix) {
        if (prefix == null) {
            return this;
        }

        return new ConfigModule(config, name, prefix);
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
        String envName;
        if (prefix == null) {
            envName = name;
        } else {
            // Insert prefix after "forage." if the name starts with it
            if (name.startsWith("forage.")) {
                envName = "forage." + prefix + "." + name.substring(7);
            } else {
                envName = prefix + "." + name;
            }
        }

        if (envName != null && !envName.isEmpty()) {
            return envName.replace(".", "_").toUpperCase();
        }

        return null;
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
        String propertyName;
        if (prefix == null) {
            propertyName = name;
        } else {
            // Insert prefix after "forage." if the name starts with it
            if (name.startsWith("forage.")) {
                propertyName = "forage." + prefix + "." + name.substring(7);
            } else {
                propertyName = prefix + "." + name;
            }
        }

        if (propertyName != null && !propertyName.isEmpty()) {
            return propertyName.replace("_", ".").toLowerCase();
        }

        return null;
    }

    public boolean match(String value) {
        if (prefix == null) {
            if (value.equals(name)) {
                return true;
            }
        } else {
            // Insert prefix after "forage." if the name starts with it
            String expectedName;
            if (name.startsWith("forage.")) {
                expectedName = "forage." + prefix + "." + name.substring(7);
            } else {
                expectedName = prefix + "." + name;
            }
            return value.equals(expectedName);
        }

        return false;
    }

    public String name() {
        if (prefix == null) {
            return name;
        } else {
            // Insert prefix after "forage." if the name starts with it
            if (name.startsWith("forage.")) {
                return "forage." + prefix + "." + name.substring(7);
            } else {
                return prefix + "." + name;
            }
        }
    }

    public Class<? extends Config> config() {
        return config;
    }

    public String description() {
        return description;
    }

    public String label() {
        return label;
    }

    public String defaultValue() {
        return defaultValue;
    }

    public String type() {
        return type;
    }

    public boolean required() {
        return required;
    }

    public ConfigTag configTag() {
        return configTag;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConfigModule that = (ConfigModule) o;
        return Objects.equals(config, that.config)
                && Objects.equals(name, that.name)
                && Objects.equals(prefix, that.prefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(config, name, prefix);
    }
}
