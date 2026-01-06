package io.kaoto.forage.agent.factory;

import org.apache.camel.component.langchain4j.agent.api.AgentConfiguration;

public interface ConfigurationAware {

    void configure(AgentConfiguration configuration);
}
