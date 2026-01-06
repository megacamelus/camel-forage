package io.kaoto.forage.agent.factory;

import org.apache.camel.Exchange;

/**
 * Base interface for different strategies for extracting the agent ID from the exchange
 */
public interface AgentSelector {

    /**
     * Given an exchange, extract the agent ID from it.
     * @param exchange the extract from which to extract the ID
     * @return the agent ID
     */
    String select(Exchange exchange);
}
