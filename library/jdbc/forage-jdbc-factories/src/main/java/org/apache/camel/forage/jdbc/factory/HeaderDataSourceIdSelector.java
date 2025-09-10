package org.apache.camel.forage.jdbc.factory;

import org.apache.camel.Exchange;

/**
 * Extracts DataSource ID from exchange headers.
 */
public class HeaderDataSourceIdSelector implements DataSourceIdSelector {

    private final String headerName;

    public HeaderDataSourceIdSelector(String headerName) {
        this.headerName = headerName;
    }

    @Override
    public String select(Exchange exchange) {
        Object headerValue = exchange.getIn().getHeader(headerName);
        if (headerValue == null) {
            throw new IllegalArgumentException(
                    String.format("Required header '%s' for DataSource ID not found in exchange", headerName));
        }
        return headerValue.toString();
    }
}
