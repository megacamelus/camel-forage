package org.apache.camel.forage.quarkus.jdbc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.camel.forage.core.common.RuntimeType;
import org.apache.camel.forage.core.util.config.ConfigStore;
import org.apache.camel.forage.jdbc.common.DataSourceCommonExportHelper;
import org.apache.camel.forage.jdbc.common.DataSourceFactoryConfig;
import org.eclipse.microprofile.config.spi.ConfigSource;

public class ForageDataSourceQuarkusConfigSource implements ConfigSource {

    private static final Map<String, String> configuration = new HashMap<>();

    static {
        // there is no need to check. whether property already exists, because the priority solves it

        // try loading multiDatasource properties
        DataSourceFactoryConfig config = new DataSourceFactoryConfig();
        Set<String> prefixes = ConfigStore.getInstance().readPrefixes(config, "(.+).jdbc\\..*");

        if (!prefixes.isEmpty()) {
            for (String name : prefixes) {
                DataSourceFactoryConfig dsFactoryConfig = new DataSourceFactoryConfig(name);
                configureDs(name, dsFactoryConfig);
            }
        } else {
            configureDs("dataSource", config);
        }
    }

    private static void configureDs(String prefix, DataSourceFactoryConfig config) {
        String property = "quarkus.datasource.";
        if (prefix != null && !prefix.isEmpty()) {
            property = property + "\"" + prefix + "\".";
        }

        configuration.put(
                property + "db-kind",
                DataSourceCommonExportHelper.getDbKindNameForRuntime(config.dbKind(), RuntimeType.quarkus));
        configuration.put(property + "password", config.password());
        configuration.put(property + "username", config.username());
        configuration.put(property + "jdbc.url", config.jdbcUrl());
        configuration.put(property + "jdbc.initial-size", String.valueOf(config.initialSize()));
        configuration.put(property + "jdbc.min-size", String.valueOf(config.minSize()));
        configuration.put(property + "jdbc.max-size", String.valueOf(config.minSize()));
        configuration.put(property + "jdbc.acquisition-timeout", config.acquisitionTimeoutSeconds() + "S");
        configuration.put(property + "jdbc.validation-query-sql", config.validationTimeoutSeconds() + "S");
        configuration.put(property + "jdbc.leak-detection-interval", config.leakTimeoutMinutes() + "M");
        //        configuration.put(
        //                property + "quarkus.transaction-manager.default-transaction-timeout",
        //                config.transactionTimeoutSeconds() + "S"); // todo need to be solved
    }

    /**
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
        return configuration.keySet();
    }

    @Override
    public String getValue(final String propertyName) {
        return configuration.get(propertyName);
    }

    @Override
    public String getName() {
        return ForageDataSourceQuarkusConfigSource.class.getSimpleName();
    }
}
