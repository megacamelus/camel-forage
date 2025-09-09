package org.apache.camel.forage.jdbc.factory;

import org.apache.camel.Exchange;

/**
 * Extracts DataSource ID from exchange variables.
 */
public class VariableDataSourceIdSelector implements DataSourceIdSelector {

    private final String variableName;

    public VariableDataSourceIdSelector(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public String select(Exchange exchange) {
        Object variableValue = exchange.getVariable(variableName);
        if (variableValue == null) {
            throw new IllegalArgumentException(
                    String.format("Required variable '%s' for DataSource ID not found in exchange", variableName));
        }
        return variableValue.toString();
    }
}
