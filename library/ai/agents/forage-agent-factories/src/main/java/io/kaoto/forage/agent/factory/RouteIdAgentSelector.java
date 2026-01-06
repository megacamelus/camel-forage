package io.kaoto.forage.agent.factory;

import org.apache.camel.Exchange;

/**
 * Agent ID source that extracts the agent ID from the exchange's route ID.
 * This is the default behavior and maintains backward compatibility.
 */
public class RouteIdAgentSelector implements AgentSelector {

    @Override
    public String select(Exchange exchange) {
        return exchange.getFromRouteId();
    }
}
