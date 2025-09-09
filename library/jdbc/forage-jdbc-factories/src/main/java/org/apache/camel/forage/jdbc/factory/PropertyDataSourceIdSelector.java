package org.apache.camel.forage.jdbc.factory;

import org.apache.camel.Exchange;

/**
 * Extracts DataSource ID from exchange properties.
 */
public class PropertyDataSourceIdSelector implements DataSourceIdSelector {

    private final String propertyName;

    public PropertyDataSourceIdSelector(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public String select(Exchange exchange) {
        Object propertyValue = exchange.getProperty(propertyName);
        if (propertyValue == null) {
            throw new IllegalArgumentException(
                    String.format("Required property '%s' for DataSource ID not found in exchange", propertyName));
        }
        return propertyValue.toString();
    }
}
