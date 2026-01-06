package io.kaoto.forage.core.util.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.apache.camel.tooling.model.Strings;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Helper class to implement a {@link org.eclipse.microprofile.config.spi.ConfigSource}.</p>
 * <p>{@link io.kaoto.forage.core.util.config.AbstractConfigSource.ConfigurationBuilder} provides a way of
 * gathering all the configuration properties without need of the conversion/validation logic</p>
 */
public abstract class AbstractConfigSource implements ConfigSource {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractConfigSource.class);

    protected abstract ConfigurationBuilder builder();

    /**
     * Quarkus ordinal priorities for the config source.
     * Value 410 grants this config source the highest priority.
     *
     * System Properties    400
     * Environment Variables from System 300
     * Environment Variables from .env file 295
     * InMemoryConfigSource 275
     * application.properties from /config 260
     * application.properties from application 250
     * microprofile-config.properties from application 100
     */
    @Override
    public int getOrdinal() {
        return 410;
    }

    @Override
    public Set<String> getPropertyNames() {
        return builder().getConfiguration().keySet();
    }

    @Override
    public String getValue(final String propertyName) {
        return builder().getConfiguration().get(propertyName);
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    public static class ConfigurationBuilder {

        private static final Map<String, String> configuration = new HashMap<>();

        public ConfigurationBuilder add(String key, Supplier<?> method) {
            Object value = method.get();
            String stringValue = String.valueOf(value);
            if (value != null && !Strings.isNullOrEmpty(stringValue)) {
                configuration.put(key, stringValue);
            }
            return this;
        }

        public ConfigurationBuilder addSecondsFromMillis(String key, Supplier<Long> method) {
            Long value = method.get();
            int intValue = (int) (value / 1000);
            return add(key, () -> intValue);
        }

        public ConfigurationBuilder add(String key, String value) {
            return add(key, () -> value);
        }

        public Map<String, String> getConfiguration() {
            return configuration;
        }
    }
}
