package io.kaoto.forage.agent.factory;

import org.apache.camel.Exchange;

/**
 * Agent ID source that extracts the agent ID from a configurable exchange property.
 * The property name is configured via the multi.agent.id.source.property property.
 */
public class PropertyAgentSelector implements AgentSelector {

    private final String propertyName;

    public PropertyAgentSelector(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public String select(Exchange exchange) {
        if (propertyName == null || propertyName.isEmpty()) {
            return null;
        }

        Object propertyValue = exchange.getProperty(propertyName);
        return propertyValue != null ? propertyValue.toString() : null;
    }

    public String getPropertyName() {
        return propertyName;
    }
}
