package io.kaoto.forage.plugin;

import io.kaoto.forage.core.common.ExportCustomizer;
import io.kaoto.forage.core.common.RuntimeType;
import io.kaoto.forage.core.util.config.Config;
import io.kaoto.forage.core.util.config.ConfigHelper;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigStore;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.jline.utils.Log;

/**
 * Helper class for all export customizers. Follow the example implementation of {@link io.kaoto.forage.plugin.jms.JmsExportCustomizer#}
 */
public abstract class AbstractExportCustomizer<T extends Config> implements ExportCustomizer {

    private Boolean enabled;

    /**
     * Returns a prefix of properties used by the exporter (for example `jms`)
     */
    protected abstract String getPrefix();

    /**
     * Returns proper instance of {@link io.kaoto.forage.core.util.config.Config} used by the exporter.
     */
    protected abstract T getConfig(String prefix);

    /**
     * Main method for obtaining all relevant dependencies based on the runtime and configuration.
     */
    protected abstract Set<String> getDependencies(RuntimeType runtime);

    T getConfig() {
        return getConfig(null);
    }

    /**
     * Returns true, if at least one property for defined prefix is present in the config,
     */
    @Override
    public boolean isEnabled() {
        if (enabled == null) {
            // to enable customizer:
            // - at least one property with required prefix has to exist or such property with prefixed with "name"
            Set<String> defaultProperties = ConfigStore.getInstance()
                    .readPrefixes(getConfig(), ConfigHelper.getDefaultPropertyRegexp(getPrefix()));
            Set<String> namedProperties = ConfigStore.getInstance()
                    .readPrefixes(getConfig(), ConfigHelper.getNamedPropertyRegexp(getPrefix()));

            if (defaultProperties.isEmpty() && namedProperties.isEmpty()) {
                Log.trace("No property for %s (%s) is present."
                        .formatted(getPrefix(), getConfig().name()));
                return enabled = false;
            }

            return enabled = true;
        } else {
            return enabled;
        }
    }

    /**
     * Return set of all values for a specific property.
     *
     *  <p>
     *      In case of entry jdbc-kind from
     *      <pre>
     *           ds1.jdbc.kind=postgresql
     *           ds2.jdbc.kind=mysql
     *       </pre>
     *       both <Strong>postgresql and mysql</Strong> are returned.
     *  </p>
     *
     *  <p>
     *      In case of entry jdbc-kind from
     *      <pre>
     *           jdbc.kind=postgresql
     *       </pre>
     *       <Strong>postgresql</Strong> is returned.
     *  </p>
     */
    protected Set<String> readAllValuesFromProperty(ConfigModule entry) {

        Set<String> named =
                ConfigStore.getInstance().readPrefixes(getConfig(), ConfigHelper.getNamedPropertyRegexp(getPrefix()));
        // values from default properties
        Set<Optional<String>> values = named.stream()
                .map(n -> {
                    // read prefixed config
                    getConfig(n);
                    return ConfigStore.getInstance().get(entry.asNamed(n));
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
        // add default value
        values.add(ConfigStore.getInstance().get(entry));

        return values.stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
    }

    @Override
    public Set<String> resolveRuntimeDependencies(RuntimeType runtime) {
        return getDependencies(runtime);
    }
}
