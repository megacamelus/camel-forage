package org.apache.camel.forage.core.util.config;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Utility class for configuration value processing and transformation in the Camel Forage framework.
 *
 * <p>This helper class provides convenient methods for processing configuration values retrieved
 * from the {@link ConfigStore}. It handles common configuration patterns such as converting
 * comma-separated strings into lists, parsing configuration values, and providing type-safe
 * configuration access.
 *
 * <p><strong>Key Features:</strong>
 * <ul>
 *   <li>Comma-separated string to list conversion</li>
 *   <li>Safe handling of missing or empty configuration values</li>
 *   <li>Integration with the Camel Forage configuration system</li>
 *   <li>Type-safe configuration value processing</li>
 * </ul>
 *
 * <p><strong>Configuration Integration:</strong>
 * This class works in conjunction with:
 * <ul>
 *   <li>{@link ConfigStore} - For configuration value storage and retrieval</li>
 *   <li>{@link ConfigModule} - For configuration module definitions</li>
 *   <li>{@link Config} implementations - For configuration management</li>
 * </ul>
 *
 * <p><strong>Common Usage Patterns:</strong>
 * <ul>
 *   <li>Converting multi-agent names configuration to lists</li>
 *   <li>Processing feature lists and capability configurations</li>
 *   <li>Handling comma-separated provider lists</li>
 * </ul>
 *
 * <p><strong>Thread Safety:</strong>
 * This utility class is stateless and thread-safe. All methods are static and can be called
 * concurrently from multiple threads without synchronization.
 *
 * @since 1.0
 * @see ConfigStore
 * @see ConfigModule
 * @see Config
 */
public final class ConfigHelper {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ConfigHelper() {}

    /**
     * Reads a configuration value as a list of strings by splitting on commas.
     *
     * <p>This method retrieves a configuration value from the ConfigStore and converts it
     * from a comma-separated string into a list of individual string values. It handles
     * missing configuration gracefully by returning an empty list.
     *
     * <p><strong>String Processing:</strong>
     * <ul>
     *   <li>Splits the configuration value on comma characters (',')</li>
     *   <li>Each resulting substring becomes a separate list item</li>
     *   <li>No trimming of whitespace is performed on individual items</li>
     *   <li>Empty strings between commas will result in empty string list items</li>
     * </ul>
     *
     * <p><strong>Usage Examples:</strong>
     * <pre>{@code
     * // Configuration: multi.agent.names=google,ollama,openai
     * ConfigModule agentNames = ConfigModule.of(MyConfig.class, "multi.agent.names");
     * List<String> names = ConfigHelper.readAsList(agentNames);
     * // Result: ["google", "ollama", "openai"]
     *
     * // Configuration: provider.features=memory,rag,guardrails
     * ConfigModule features = ConfigModule.of(MyConfig.class, "provider.features");
     * List<String> featureList = ConfigHelper.readAsList(features);
     * // Result: ["memory", "rag", "guardrails"]
     *
     * // Missing configuration
     * ConfigModule missing = ConfigModule.of(MyConfig.class, "not.configured");
     * List<String> empty = ConfigHelper.readAsList(missing);
     * // Result: [] (empty list)
     * }</pre>
     *
     * <p><strong>Error Handling:</strong>
     * <ul>
     *   <li>Missing configuration values return an empty list (not null)</li>
     *   <li>Empty string configuration values result in a single empty string in the list</li>
     *   <li>Configuration values with no commas result in a single-item list</li>
     * </ul>
     *
     * @param configModule the configuration module to read from
     * @return a list of strings parsed from the comma-separated configuration value,
     *         or an empty list if the configuration is not set
     *
     * @throws NullPointerException if configModule is null
     *
     * @see ConfigStore#get(ConfigModule)
     * @see List#of(Object[])
     * @see String#split(String)
     */
    public static List<String> readAsList(ConfigModule configModule) {
        final Optional<String> valueHolder = ConfigStore.getInstance().get(configModule);
        if (valueHolder.isEmpty()) {
            return Collections.emptyList();
        }

        String valueList = valueHolder.get();
        return List.of(valueList.split(","));
    }
}
