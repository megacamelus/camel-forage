package org.apache.camel.forage.core.util.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized configuration store for the Camel Forage framework that manages configuration values
 * from multiple sources with a defined precedence hierarchy.
 *
 * <p>The ConfigStore implements a singleton pattern and serves as the central repository for all
 * configuration values in the application. It supports loading configuration from multiple sources
 * and provides a consistent API for accessing configuration values.
 *
 * <p><strong>Configuration Source Precedence (highest to lowest):</strong>
 * <ol>
 *   <li>Environment variables</li>
 *   <li>System properties</li>
 *   <li>Configuration files (loaded via URL or classpath)</li>
 * </ol>
 *
 * <p>The store automatically resolves configuration values by checking sources in the above order,
 * returning the first non-null value found. This allows for flexible configuration management where
 * environment-specific values can override defaults without code changes.
 *
 * <p><strong>Usage Example:</strong>
 * <pre>{@code
 * // Register configuration entries
 * ConfigModule apiKey = ConfigModule.of(MyConfig.class, "api-key");
 * ConfigEntry entry = ConfigEntry.fromEnv("MY_API_KEY");
 * ConfigStore.getInstance().add(apiKey, entry);
 *
 * // Retrieve configuration values
 * String value = ConfigStore.getInstance().get(apiKey)
 *         .orElseThrow(() -> new MissingConfigException("API key not configured"));
 * }</pre>
 *
 * <p><strong>Thread Safety:</strong>
 * This class is thread-safe for concurrent reads after initialization. However, configuration
 * registration (add methods) should typically be performed during application startup before
 * concurrent access begins.
 *
 * @see Config
 * @see ConfigModule
 * @see ConfigEntry
 * @since 1.0
 */
public final class ConfigStore {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigStore.class);

    private static ConfigStore INSTANCE;
    private final Properties properties = new Properties();
    private ClassLoader classLoader;

    /**
     * Private constructor to enforce singleton pattern.
     */
    private ConfigStore() {}

    /**
     * Returns the singleton instance of the ConfigStore.
     *
     * <p>This method is thread-safe and implements lazy initialization. The same instance
     * will be returned for all calls within the same JVM.
     *
     * @return the singleton ConfigStore instance
     */
    public static synchronized ConfigStore getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }

        INSTANCE = new ConfigStore();
        return INSTANCE;
    }

    /**
     * Loads the configuration from the given module
     *
     * <p>This method attempts to resolve a value for the given ConfigEntry by checking
     * environment variables and system properties in order of precedence. If a value
     * is found, it is stored in the internal properties using the ConfigModule as the key.
     *
     * <p>If no value is found from any source, nothing is stored, and subsequent calls
     * to {@link #get(ConfigModule)} will return an empty Optional.
     *
     * @param module the configuration module that serves as the key
     */
    public void load(ConfigModule module) {
        final Optional<String> read = tryRead(module);

        read.ifPresent(s -> properties.put(module, s));
    }

    /**
     * Loads the configuration from the class' associated properties file.
     *
     * <p>This method looks for a properties file named after the configuration instance's
     * {@link Config#name()} method in the same package as the configuration class. If found,
     * the properties are loaded and added to the store.
     *
     * <p>For example, if the config name is "my-module", it will look for "my-module.properties"
     * in the classpath relative to the configuration class.
     *
     * @param clazz the configuration class
     * @param instance the configuration instance
     * @param <T> the type of the configuration class
     */
    public <T extends Config> void load(Class<T> clazz, T instance, BiConsumer<String, String> registerFunction) {
        final String fileName = asProperties(instance);
        LOG.info("Adding {} to {}", clazz, fileName);

        loadProperties(registerFunction, loadPropertiesWithPriority(instance, fileName));
    }

    private static void loadProperties(BiConsumer<String, String> registerFunction, Properties props) {
        props.forEach((k, v) -> registerFunction.accept((String) k, (String) v));
    }

    /**
     * Utility method to read common prefixes from the {@link Config}, defined by the regexp.
     *
     * <p>
     *     Regexp has to contain one group, which is extracted.
     *     For the regexp <pre>"(.+).jdbc\\..*"</pre> from the properties:
     *      <pre>
     *          ds1.jdbc.url=jdbc:postgresql://localhost:5432/postgres
     *          ds2.jdbc.url=jdbc:mysql://localhost:3306/test
     *      </pre>
     *      both <Strong>ds1, ds2</Strong> prefixes are extracted.
     *      </p>
     * </p>
     *
     * @return If there is no group extracted in the whole properties file, null is return. Else prefixes defined by
     * the regexp in a set.
     */
    public <T extends Config> Set<String> readPrefixes(T instance, String regexp) {
        final String fileName = asProperties(instance);

        return readPrefixes(loadPropertiesWithPriority(instance, fileName), regexp);
    }

    /**
     * Method for loading properties from different sources in proper order, defaulting to 'default' properties.
     *
     * <ul>
     *     <li>File from a directory defined via properties `forage.config.dir` or `FORAGE_CONFIG_DIR`</li>
     *     <li>File in the working directory</li>
     *     <li>Properties read via specific classloader</li>
     *     <li>Properties loaded by a default classloader</li>
     * </ul>
     *
     * <p>Be aware, that <pre>Thread.currentThread().getContextClassLoader()</pre> has to be used as default classloader
     * (to work as expected in Quarkus runtime)</p>
     */
    private <T extends Config> Properties loadPropertiesWithPriority(T instance, String fileName) {
        InputStream is = null;
        File file = Paths.get("", fileName).toAbsolutePath().toFile();
        if (!file.exists()) {
            final String property = System.getProperty("forage.config.dir");
            if (property != null) {
                file = Paths.get(property, fileName).toAbsolutePath().toFile();
            } else {
                final String environment = System.getenv("FORAGE_CONFIG_DIR");
                if (environment != null) {
                    file = Paths.get(environment, fileName).toAbsolutePath().toFile();
                }
            }
        }

        if (file.exists()) {
            try {
                is = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        if (is == null && classLoader != null) {
            LOG.info("Trying to use the classloader to read {}", file);
            final URL resource = classLoader.getResource(asClasspathPath(instance));
            if (resource != null) {
                try {
                    is = resource.openStream();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (is == null) {
            LOG.info("Loading defaults from the forage component");
            is = classLoader == null
                    ? ConfigStore.class.getResourceAsStream("/" + instance.name() + ".properties")
                    : classLoader.getResourceAsStream("/" + instance.name() + ".properties");
        }

        try {
            Properties props = new Properties();
            if (is != null) {
                LOG.info("Loading defaults from the forage component");
                props.load(is);
            }
            return props;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
        }
    }

    private static Set<String> readPrefixes(Properties props, String regexp) {
        Pattern pattern = Pattern.compile(regexp);

        return Collections.list(props.keys()).stream()
                .map((key) -> {
                    Matcher m = pattern.matcher((String) key);
                    if (m.find()) {
                        return m.group(1);
                    } else {
                        return null;
                    }
                })
                .filter(prefix -> prefix != null)
                .collect(Collectors.toSet());
    }

    private static <T extends Config> String asClasspathPath(T instance) {
        return instance.getClass().getPackageName().replace(".", "/") + "/" + instance.name() + ".properties";
    }

    private static <T extends Config> String asProperties(T instance) {
        return "./" + instance.name() + ".properties";
    }

    /**
     * Reads a configuration value from the sources defined in the ConfigEntry.
     *
     * <p>This method implements the configuration source precedence by checking:
     * <ol>
     *   <li>Environment variables (via {@link System#getenv(String)})</li>
     *   <li>System properties (via {@link System#getProperty(String)})</li>
     * </ol>
     *
     * <p>The first non-null value found is returned. If no value is found from any source,
     * an empty Optional is returned.
     *
     * @return an Optional containing the configuration value, or empty if not found
     */
    private Optional<String> tryRead(ConfigModule module) {
        final String environmentValue = System.getenv(module.envName());
        if (environmentValue != null) {
            return Optional.of(environmentValue);
        }

        final String propertyValue = System.getProperty(module.propertyName());
        if (propertyValue != null) {
            return Optional.of(propertyValue);
        }

        Optional<String> value = null;
        switch (ConfigHelper.getRuntime()) {
            case springBoot:
                value = ConfigHelper.getSpringBootProperty(module.propertyName());
                break;
            case quarkus:
                value = ConfigHelper.getQuarkusProperty(module.propertyName());
                break;
            case main:
                value = ConfigHelper.getCamelMainProperty(module.propertyName());
                break;
        }

        if (value != null && (!value.isEmpty())) {
            return value;
        }

        return Optional.empty();
    }

    /**
     * Retrieves a configuration value for the specified ConfigModule.
     *
     * <p>This method returns the configuration value that was previously stored for the
     * given ConfigModule, either through direct registration via {@link #load(ConfigModule)}
     * or through properties loaded from files.
     *
     * <p>If no value was found during registration or if the ConfigModule was never registered,
     * an empty Optional is returned.
     *
     * @param entry the configuration module to look up
     * @return an Optional containing the configuration value, or empty if not found
     */
    public Optional<String> get(ConfigModule entry) {
        return Optional.ofNullable((String) properties.get(entry));
    }

    /**
     * Sets a configuration value directly for the specified ConfigModule.
     *
     * <p>This method allows direct assignment of configuration values, bypassing the normal
     * configuration source resolution process. It immediately stores the provided value in
     * the internal properties store, overriding any previously stored value for the same
     * ConfigModule.
     *
     * <p>This method is primarily used by:
     * <ul>
     *   <li>Dynamic configuration registration through {@link Config#register(String, String)}</li>
     *   <li>Configuration loading from property files during startup</li>
     *   <li>Runtime configuration updates in specific scenarios</li>
     *   <li>Testing scenarios where configuration values need to be controlled directly</li>
     * </ul>
     *
     * <p><strong>Usage Context:</strong>
     * Unlike the {@link #load(ConfigModule)} method which resolves values from
     * environment variables and system properties, this method directly sets the value without
     * any source resolution. This makes it suitable for scenarios where the value has already
     * been resolved or comes from a different source (like configuration files).
     *
     * <p><strong>Example Usage:</strong>
     * <pre>{@code
     * // Direct value assignment (typically from Config.register implementations)
     * ConfigModule apiKey = ConfigModule.of(MyConfig.class, "api.key");
     * ConfigStore.getInstance().set(apiKey, "resolved-api-key-value");
     *
     * // The value is immediately available for retrieval
     * String value = ConfigStore.getInstance().get(apiKey).orElse("default");
     * }</pre>
     *
     * <p><strong>Precedence Override:</strong>
     * Values set through this method will override any values that might have been previously
     * registered through environment variables or system properties for the same ConfigModule.
     * Subsequent calls to {@link #get(ConfigModule)} will return the value set by this method.
     *
     * <p><strong>Thread Safety:</strong>
     * This method is not thread-safe. If concurrent access is required, external synchronization
     * should be used. In typical usage, configuration values are set during application startup
     * before concurrent access begins.
     *
     * @param module the configuration module that serves as the key for storing the value
     * @param value the configuration value to store; may be {@code null} to remove the configuration
     * @see #load(ConfigModule)
     * @see #get(ConfigModule)
     * @see Config#register(String, String)
     * @since 1.0
     */
    public void set(ConfigModule module, String value) {
        properties.put(module, value);
    }

    /**
     * Sets a configuration value directly by string key.
     *
     * <p>This method bypasses the ConfigModule lookup and stores the value directly
     * in the internal properties using the provided key. This is useful when mapping
     * configuration values between different namespaces.
     *
     * @param key the configuration key (e.g., "google.api.key")
     * @param value the configuration value to store
     * @since 1.0
     */
    public void setDirect(String key, String value) {
        properties.put(key, value);
    }

    /**
     * Gets a configuration value directly by string key.
     *
     * <p>This method bypasses the ConfigModule lookup and retrieves the value directly
     * from the internal properties using the provided key.
     *
     * @param key the configuration key (e.g., "google.api.key")
     * @return an Optional containing the value if present, or empty if not found
     * @since 1.0
     */
    public Optional<String> getDirect(String key) {
        return Optional.ofNullable((String) properties.get(key));
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
