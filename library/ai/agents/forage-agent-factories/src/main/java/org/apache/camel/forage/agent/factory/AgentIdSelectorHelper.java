package org.apache.camel.forage.agent.factory;

import org.apache.camel.Exchange;
import org.apache.camel.forage.core.exceptions.UndefinedAgentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for creating the appropriate agent selector implementation based on string identifiers.
 * Supports creating different strategies for extracting agent IDs from exchanges.
 */
public class AgentIdSelectorHelper {
    private static final Logger LOG = LoggerFactory.getLogger(AgentIdSelectorHelper.class);

    /**
     * Creates an AgentIdSource implementation based on the specified source type.
     *
     * @param config The MultiAgentConfig containing configuration for the source
     * @return An appropriate AgentIdSource implementation
     * @throws IllegalArgumentException if the source type is unknown or unsupported
     */
    private static AgentSelector create(MultiAgentConfig config) {
        String sourceType = config.multiAgentIdSource();

        switch (sourceType.toLowerCase()) {
            case MultiAgentConfig.ROUTE_ID:
                return new RouteIdAgentSelector();

            case MultiAgentConfig.HEADER:
                String headerName = config.multiAgentIdSourceHeader();
                if (headerName == null || headerName.isEmpty()) {
                    throw new IllegalArgumentException(
                            "Header name must be configured via multi.agent.id.source.header when using header source type");
                }
                return new HeaderAgentSelector(headerName);

            case MultiAgentConfig.PROPERTY:
                String propertyName = config.multiAgentIdSourceProperty();
                if (propertyName == null || propertyName.isEmpty()) {
                    throw new IllegalArgumentException(
                            "Property name must be configured via multi.agent.id.source.property when using property source type");
                }
                return new PropertyAgentSelector(propertyName);

            case MultiAgentConfig.VARIABLE:
                String variableName = config.multiAgentIdSourceVariable();
                if (variableName == null || variableName.isEmpty()) {
                    throw new IllegalArgumentException(
                            "Variable name must be configured via multi.agent.id.source.variable when using variable source type");
                }
                return new VariableAgentSelector(variableName);

            default:
                throw new IllegalArgumentException("Unknown agent ID source type: " + sourceType
                        + ". Supported types are: " + MultiAgentConfig.ROUTE_ID + ", " + MultiAgentConfig.HEADER + ", "
                        + MultiAgentConfig.PROPERTY + ", " + MultiAgentConfig.VARIABLE);
        }
    }

    /**
     * Throws an appropriate UndefinedAgentException based on the agent ID source type
     * configured in the MultiAgentConfig and the extracted agent ID value from the exchange.
     *
     * @param config   The MultiAgentConfig containing the source type configuration
     * @param exchange The Exchange from which to extract the agent ID value
     * @return
     * @throws UndefinedAgentException Always throws - this method never returns normally
     */
    public static UndefinedAgentException newUndefinedAgentException(MultiAgentConfig config, Exchange exchange)
            throws UndefinedAgentException {
        String sourceType = config.multiAgentIdSource();

        switch (sourceType.toLowerCase()) {
            case MultiAgentConfig.ROUTE_ID:
                String routeId = exchange.getFromRouteId();
                return UndefinedAgentException.fromRouteId(routeId);

            case MultiAgentConfig.HEADER:
                String headerName = config.multiAgentIdSourceHeader();
                String headerValue = null;
                if (headerName != null && !headerName.isEmpty()) {
                    headerValue = exchange.getIn().getHeader(headerName, String.class);
                }
                return UndefinedAgentException.fromHeader(headerName, headerValue);

            case MultiAgentConfig.PROPERTY:
                String propertyName = config.multiAgentIdSourceProperty();
                String propertyValue = null;
                if (propertyName != null && !propertyName.isEmpty()) {
                    propertyValue = exchange.getProperty(propertyName, String.class);
                }
                return UndefinedAgentException.fromProperty(propertyName, propertyValue);

            case MultiAgentConfig.VARIABLE:
                String variableName = config.multiAgentIdSourceVariable();
                String variableValue = null;
                if (variableName != null && !variableName.isEmpty()) {
                    variableValue = exchange.getVariable(variableName, String.class);
                }
                return UndefinedAgentException.fromVariable(variableName, variableValue);

            default:
                // Fallback to route ID if source type is unknown
                String fallbackRouteId = exchange.getFromRouteId();
                return UndefinedAgentException.fromRouteId(fallbackRouteId);
        }
    }

    public static String select(MultiAgentConfig config, Exchange exchange) {
        AgentSelector agentSelector = AgentIdSelectorHelper.create(config);
        String agentId = agentSelector.select(exchange);
        LOG.info("Creating Agent for {} using ID {}", exchange.getExchangeId(), agentId);
        return agentId;
    }
}
