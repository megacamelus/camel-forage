package org.apache.camel.forage.core.util.config;

import io.smallrye.config.SmallRyeConfig;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import org.apache.camel.forage.core.common.RuntimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOG = LoggerFactory.getLogger(ConfigHelper.class);

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ConfigHelper() {}

    private static RuntimeType runtime = null;
    private static SmallRyeConfig quarkusConfig = null;
    private static Properties springBootConfig = null;
    private static Properties camelConfig = null;

    public static RuntimeType getRuntime() {
        if (runtime == null) {
            if (isRuntimeSpringBoot()) {
                runtime = RuntimeType.springBoot;
            } else if (isRuntimeQuarkus()) {
                runtime = RuntimeType.quarkus;
            } else {
                runtime = RuntimeType.main;
            }
        }
        return runtime;
    }

    public static Optional<String> getSpringBootProperty(String propertyName) {
        Properties springBootProps = ConfigHelper.getSpringBootConfig();
        if (springBootProps != null) {
            LOG.info("Loading {} from Spring Boot", propertyName);
            String propertyValue = (String) springBootProps.get(propertyName);
            if (propertyValue != null) {
                return Optional.of(propertyValue);
            }
        }
        return null;
    }

    public static Optional<String> getQuarkusProperty(String propertyName) {
        SmallRyeConfig config = ConfigHelper.getQuarkusConfig();
        if (config != null) {
            LOG.info("Loading {} from Quarkus", propertyName);
            Optional<String> quarkusValue = config.getOptionalValue(propertyName, String.class);
            if (quarkusValue != null) {
                return quarkusValue;
            }
        }
        return null;
    }

    public static Optional<String> getCamelMainProperty(String propertyName) {
        Properties camelMainProps = ConfigHelper.getCamelMainConfig();
        if (camelMainProps != null) {
            LOG.info("Loading {} from Camel main", propertyName);
            String mainPropertyValue = (String) camelMainProps.get(propertyName);
            if (mainPropertyValue != null) {
                return Optional.of(mainPropertyValue);
            }
        }
        return null;
    }

    private static boolean classExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isRuntimeSpringBoot() {
        boolean classExists = classExists("org.springframework.boot.SpringBootVersion");
        if (classExists) {
            try {
                Class<?> versionClass = Class.forName("org.springframework.boot.SpringBootVersion");
                Method getVersionMethod = versionClass.getDeclaredMethod("getVersion");

                Object result = getVersionMethod.invoke(null);

                if (result != null) {
                    String version = result.toString();
                    LOG.info("Spring Boot environment detected, {} version", version);
                    return true;
                }
            } catch (ClassNotFoundException e) {
            } catch (Exception e) {
            }
        }
        return false;
    }

    public static boolean isRuntimeQuarkus() {
        boolean classExists = classExists("io.quarkus.runtime.Application");

        if (classExists) {
            try {
                Class<?> quarkusClass = Class.forName("io.quarkus.runtime.Application");
                Method getName =
                        quarkusClass.getMethod("getName"); // if we can find this method, declare runtime quarkus
                LOG.info("Quarkus environment detected");

                return true;
            } catch (ClassNotFoundException e) {
            } catch (Exception e) {
            }
        }
        return false;
    }

    private static SmallRyeConfig getQuarkusConfig() {
        if (quarkusConfig != null) {
            return quarkusConfig;
        } else {
            quarkusConfig = (SmallRyeConfig)
                    org.eclipse.microprofile.config.ConfigProvider.getConfig().unwrap(SmallRyeConfig.class);
            return quarkusConfig;
        }
    }

    private static Properties getSpringBootConfig() {
        if (springBootConfig != null) {
            return springBootConfig;
        } else {
            try (InputStream input =
                    ConfigHelper.class.getClassLoader().getResourceAsStream("application.properties")) {
                if (input == null) {
                    return null;
                }
                springBootConfig = new Properties();
                springBootConfig.load(input);
            } catch (IOException ex) {
            }
            return springBootConfig;
        }
    }

    private static Properties getCamelMainConfig() {
        if (camelConfig != null) {
            return camelConfig;
        } else {
            try (InputStream input =
                    ConfigHelper.class.getClassLoader().getResourceAsStream("application.properties")) {
                if (input == null) {
                    return null;
                }
                camelConfig = new Properties();
                camelConfig.load(input);
            } catch (IOException ex) {
            }
            return camelConfig;
        }
    }

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
