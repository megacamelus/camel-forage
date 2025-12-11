package org.apache.camel.forage.agent;

import java.util.List;
import org.apache.camel.CamelContext;
import org.apache.camel.component.langchain4j.agent.api.Agent;
import org.apache.camel.forage.agent.factory.DefaultAgentFactory;
import org.apache.camel.forage.agent.factory.MultiAgentConfig;
import org.apache.camel.forage.agent.factory.MultiAgentFactory;
import org.apache.camel.forage.core.annotations.ForageFactory;
import org.apache.camel.forage.core.common.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BeanFactory that registers Agent beans into the CamelContext registry.
 * Delegates to either DefaultAgentFactory or MultiAgentFactory based on
 * configuration to create and register agents for use in Camel routes.
 *
 * <p>If multi.agent.names is configured, uses MultiAgentFactory for multi-agent
 * scenarios. Otherwise, uses DefaultAgentFactory for single-agent setups.
 */
@ForageFactory(
        value = "CamelAgentFactory",
        components = {"camel-langchain4j-agent"},
        description = "Agent bean factory delegating to DefaultAgentFactory or MultiAgentFactory",
        factoryType = "Agent",
        autowired = true)
public class AgentBeanFactory implements BeanFactory {
    private static final Logger LOG = LoggerFactory.getLogger(AgentBeanFactory.class);

    private CamelContext camelContext;
    private static final String DEFAULT_AGENT = "agent";

    @Override
    public void configure() {
        MultiAgentConfig multiAgentConfig = new MultiAgentConfig();
        List<String> multiAgentNames = multiAgentConfig.multiAgentNames();

        if (multiAgentNames != null && !multiAgentNames.isEmpty()) {
            try {
                configureMultiAgent(multiAgentNames);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        } else {
            configureSingleAgent();
        }
    }

    private void configureMultiAgent(List<String> agentNames) throws Exception {
        LOG.info("Configuring multi-agent mode with agents: {}", agentNames);

        MultiAgentFactory multiAgentFactory = new MultiAgentFactory();
        multiAgentFactory.setCamelContext(camelContext);

        // Pre-create and register individual agents for each configured name
        for (String agentName : agentNames) {
            if (camelContext.getRegistry().lookupByNameAndType(agentName, Agent.class) == null) {

                Agent agent = multiAgentFactory.createAgent(null, agentName);
                if (agent != null) {
                    camelContext.getRegistry().bind(agentName, agent);
                    LOG.info("Registered Agent bean with name: {}", agentName);
                }
            }
        }
    }

    private void configureSingleAgent() {
        LOG.info("Configuring single-agent mode");

        DefaultAgentFactory defaultAgentFactory = new DefaultAgentFactory();
        defaultAgentFactory.setCamelContext(camelContext);

        // Create and register a default agent
        if (camelContext.getRegistry().lookupByNameAndType(DEFAULT_AGENT, Agent.class) == null) {
            try {
                Agent agent = defaultAgentFactory.createAgent();
                if (agent != null) {
                    camelContext.getRegistry().bind(DEFAULT_AGENT, agent);
                    LOG.info("Registered default Agent bean with name: {}", DEFAULT_AGENT);
                }
            } catch (Exception e) {
                LOG.warn("Failed to create default agent: {}", e.getMessage());
                LOG.debug("Agent creation exception details", e);
            }
        }
    }

    @Override
    public void setCamelContext(CamelContext camelContext) {
        this.camelContext = camelContext;
    }

    @Override
    public CamelContext getCamelContext() {
        return camelContext;
    }
}
