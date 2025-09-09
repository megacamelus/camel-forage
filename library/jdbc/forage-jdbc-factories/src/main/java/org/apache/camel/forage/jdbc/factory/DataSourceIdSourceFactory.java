package org.apache.camel.forage.jdbc.factory;

import org.apache.camel.Exchange;

/**
 * Factory for creating DataSource ID source implementations based on configuration.
 */
public class DataSourceIdSourceFactory {

    public static DataSourceIdSelector create(MultiDataSourceConfig config) {
        String sourceType = config.dataSourceIdSourceType();
        String sourceName = config.dataSourceIdSourceName();

        return switch (sourceType.toLowerCase()) {
            case "header" -> new HeaderDataSourceIdSelector(sourceName);
            case "property" -> new PropertyDataSourceIdSelector(sourceName);
            case "routeid" -> new RouteIdDataSourceIdSelector();
            case "variable" -> new VariableDataSourceIdSelector(sourceName);
            default -> throw new IllegalArgumentException("Unknown DataSource ID source type: " + sourceType);
        };
    }

    public static RuntimeException newUndefinedDataSourceException(MultiDataSourceConfig config, Exchange exchange) {
        DataSourceIdSelector idSource = create(config);
        String extractedId = idSource.select(exchange);

        return new IllegalArgumentException(String.format(
                "DataSource '%s' is not defined in multi.datasource.names configuration. "
                        + "Available DataSources: %s",
                extractedId, config.multiDataSourceNames()));
    }
}
