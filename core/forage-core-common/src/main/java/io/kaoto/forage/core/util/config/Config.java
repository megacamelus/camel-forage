package io.kaoto.forage.core.util.config;

/**
 * Base interface for module configuration in the Forage framework.
 *
 * <p>This interface defines the fundamental contract that all module configurations must implement.
 * It serves as the foundation for a hierarchical configuration system where each module can define
 * its own configuration parameters and access them through a centralized {@link ConfigStore}.
 *
 * <p>Implementations of this interface are typically used to:
 * <ul>
 *   <li>Define module-specific configuration parameters</li>
 *   <li>Provide a unique identifier for the module</li>
 *   <li>Register configuration entries with the {@link ConfigStore}</li>
 *   <li>Access configuration values from environment variables or system properties</li>
 * </ul>
 *
 * <p>Configuration values can be sourced from multiple locations in order of precedence:
 * <ol>
 *   <li>Environment variables</li>
 *   <li>System properties</li>
 *   <li>Configuration files</li>
 * </ol>
 *
 * <p><strong>Example implementation:</strong>
 * <pre>{@code
 * public class MyModuleConfig implements Config {
 *     private static final ConfigModule API_KEY = ConfigModule.of(MyModuleConfig.class, "api-key");
 *
 *     public MyModuleConfig() {
 *         ConfigStore.getInstance().add(API_KEY, ConfigEntry.fromEnv("MY_MODULE_API_KEY"));
 *         ConfigStore.getInstance().add(MyModuleConfig.class, this);
 *     }
 *
 *     @Override
 *     public String name() {
 *         return "my-module";
 *     }
 *
 *     public String apiKey() {
 *         return ConfigStore.getInstance()
 *                 .get(API_KEY)
 *                 .orElseThrow(() -> new MissingConfigException("Missing API key"));
 *     }
 * }
 * }</pre>
 *
 * @see ConfigStore
 * @see ConfigEntry
 * @see ConfigModule
 * @since 1.0
 */
public interface Config {

    /**
     * Returns the unique name identifier for this module configuration.
     *
     * <p>This name is used to identify the module and typically corresponds to the
     * module's artifact name or a logical module identifier. It should be unique
     * across all modules in the system to avoid configuration conflicts.
     *
     * <p>The name is also used when loading configuration files, where a file
     * named "{@code <name>.properties}" will be automatically loaded if present
     * in the classpath.
     *
     * @return the unique name of this module configuration, never {@code null}
     */
    String name();

    /**
     * Registers a configuration value for the specified property name.
     *
     * <p>This method is called by the configuration system to dynamically register
     * configuration values that have been loaded from configuration files or other
     * external sources. It provides a mechanism for modules to receive and process
     * configuration properties beyond the standard environment variables and system
     * properties.
     *
     * <p>The method is typically invoked during the configuration loading process,
     * where property files are parsed and individual key-value pairs are registered
     * with the appropriate configuration modules. The implementation should:
     * <ul>
     *   <li>Resolve the property name to the corresponding {@link ConfigModule}</li>
     *   <li>Create a {@link ConfigEntry} from the provided value</li>
     *   <li>Register the entry with the {@link ConfigStore}</li>
     *   <li>Handle unknown property names appropriately (typically by throwing an exception)</li>
     * </ul>
     *
     * <p><strong>Example implementation:</strong>
     * <pre>{@code
     * public void register(String name, String value) {
     *     ConfigModule config = resolve(name);  // Resolve property name to ConfigModule
     *     ConfigStore.getInstance().add(config, ConfigEntry.fromProperty(value));
     * }
     *
     * private ConfigModule resolve(String name) {
     *     if (API_KEY.name().equals(name)) {
     *         return API_KEY;
     *     }
     *     if (MODEL_NAME.name().equals(name)) {
     *         return MODEL_NAME;
     *     }
     *     throw new IllegalArgumentException("Unknown config entry: " + name);
     * }
     * }</pre>
     *
     * <p><strong>Property Name Mapping:</strong>
     * The property names passed to this method typically follow a dot-notation convention
     * corresponding to system property names. For example:
     * <ul>
     *   <li>{@code "openai.api.key"} for OpenAI API key configuration</li>
     *   <li>{@code "ollama.model.name"} for Ollama model name configuration</li>
     *   <li>{@code "google.api.key"} for Google API key configuration</li>
     * </ul>
     *
     * <p><strong>Error Handling:</strong>
     * Implementations should throw appropriate exceptions for:
     * <ul>
     *   <li>Unknown property names - typically {@link IllegalArgumentException}</li>
     *   <li>Invalid property values - depending on the validation requirements</li>
     * </ul>
     *
     * @param name the configuration property name, typically in dot notation (e.g., "module.property.name")
     * @param value the configuration value to register, may be {@code null} if the property allows it
     * @throws IllegalArgumentException if the property name is not recognized by this configuration module
     * @see #add(ConfigModule)
     * @see ConfigEntry#fromProperty(String)
     * @see ConfigModule
     * @since 1.0
     */
    void register(String name, String value);
}
