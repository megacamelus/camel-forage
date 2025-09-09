package org.apache.camel.forage.jdbc.factory;

import org.apache.camel.Exchange;

/**
 * Extracts DataSource ID from the route ID of the exchange.
 */
public class RouteIdDataSourceIdSelector implements DataSourceIdSelector {

    @Override
    public String select(Exchange exchange) {
        String routeId = exchange.getFromRouteId();
        if (routeId == null) {
            throw new IllegalArgumentException("Route ID not available in exchange for DataSource ID extraction");
        }
        return routeId;
    }
}
