package org.apache.camel.forage.jdbc.factory;

import org.apache.camel.Exchange;

/**
 * Interface for extracting DataSource identifiers from Camel exchanges.
 * Different implementations can extract the ID from headers, properties, route context, etc.
 */
public interface DataSourceIdSelector {

    /**
     * Extracts the DataSource identifier from the given exchange.
     *
     * @param exchange the Camel exchange to extract the ID from
     * @return the DataSource identifier string
     */
    String select(Exchange exchange);
}
