package org.apache.camel.forage.jdbc;

import java.util.List;
import java.util.Optional;
import org.apache.camel.forage.core.util.config.Config;
import org.apache.camel.forage.core.util.config.ConfigHelper;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigStore;

/**
 * Configuration for multi-DataSource factory with support for multiple named DataSource instances.
 */
public class MultiDataSourceConfig implements Config {

    private final String prefix;

    public MultiDataSourceConfig() {
        this(null);
    }

    public MultiDataSourceConfig(String prefix) {
        this.prefix = prefix;

        // First register new configuration modules. This happens only if a prefix is provided
        MultiDataSourceConfigEntries.register(prefix);

        // Then, loads the configurations from the properties file associated with this Config module
        ConfigStore.getInstance().load(MultiDataSourceConfig.class, this, this::register);

        // Lastly, load the overrides defined in system properties and environment variables
        MultiDataSourceConfigEntries.loadOverrides(prefix);
    }

    @Override
    public String name() {
        return "forage-multi-datasource-factory";
    }

    @Override
    public void register(String name, String value) {
        Optional<ConfigModule> config = MultiDataSourceConfigEntries.find(prefix, name);

        config.ifPresent(module -> ConfigStore.getInstance().set(module, value));
    }

    public List<String> multiDataSourceNames() {
        return ConfigHelper.readAsList(MultiDataSourceConfigEntries.MULTI_DATASOURCE_NAMES.asNamed(prefix));
    }
}
