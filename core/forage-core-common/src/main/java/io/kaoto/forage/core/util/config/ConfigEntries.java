package io.kaoto.forage.core.util.config;

import java.util.Map;
import java.util.Optional;

public abstract class ConfigEntries {

    protected static Optional<ConfigModule> find(
            Map<ConfigModule, ConfigEntry> configModules, String prefix, String name) {
        return configModules.entrySet().stream()
                .filter(e -> e.getKey().match(name))
                .findFirst()
                .map(c -> c.getKey());
    }

    /**
     * Tries loading the configuration represented by the set of configuration modules into the store
     * @param configModules the set of configuration modules to try loading into the configuration store
     * @param prefix an optional prefix for the configuration
     */
    protected static void load(Map<ConfigModule, ConfigEntry> configModules, String prefix) {
        configModules.forEach((k, v) -> ConfigStore.getInstance().load(k.asNamed(prefix)));
    }
}
