package org.apache.camel.forage.agent.factory;

import static org.apache.camel.forage.agent.factory.MultiAgentConfigEntries.MULTI_AGENT_ID_SOURCE;
import static org.apache.camel.forage.agent.factory.MultiAgentConfigEntries.MULTI_AGENT_ID_SOURCE_HEADER;
import static org.apache.camel.forage.agent.factory.MultiAgentConfigEntries.MULTI_AGENT_ID_SOURCE_PROPERTY;
import static org.apache.camel.forage.agent.factory.MultiAgentConfigEntries.MULTI_AGENT_ID_SOURCE_VARIABLE;
import static org.apache.camel.forage.agent.factory.MultiAgentConfigEntries.MULTI_AGENT_NAMES;

import java.util.List;
import java.util.Optional;
import org.apache.camel.forage.core.util.config.Config;
import org.apache.camel.forage.core.util.config.ConfigHelper;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigStore;

/**
 * Configuration class for multi-agent orchestration within the Camel Forage framework.
 *
 * <p>This class manages configuration settings for multi-agent scenarios, including agent
 * discovery, naming, and agent ID extraction strategies. It provides the foundation for
 * sophisticated multi-agent routing and coordination.
 *
 * <p>This configuration is used by the MultiAgentFactory for dynamic agent selection and routing
 * based on various extraction strategies, enabling flexible multi-agent architectures.
 */
public class MultiAgentConfig implements Config {

    /**
     * Source type constants for agent ID extraction strategies.
     */
    public static final String ROUTE_ID = "route";

    public static final String HEADER = "header";
    public static final String PROPERTY = "property";
    public static final String VARIABLE = "variable";
    private final String prefix;

    public MultiAgentConfig() {
        this(null);
    }

    public MultiAgentConfig(String prefix) {
        this.prefix = prefix;

        // First register new configuration modules. This happens only if a prefix is provided
        MultiAgentConfigEntries.register(prefix);

        // Then, loads the configurations from the properties file associated with this Config module
        ConfigStore.getInstance().load(MultiAgentConfig.class, this, this::register);

        // Lastly, load the overrides defined in system properties and environment variables
        MultiAgentConfigEntries.loadOverrides(prefix);
    }

    @Override
    public void register(String name, String value) {
        Optional<ConfigModule> config = MultiAgentConfigEntries.find(prefix, name);

        config.ifPresent(module -> ConfigStore.getInstance().set(module, value));
    }

    @Override
    public String name() {
        return "forage-agent-factory-default";
    }

    public List<String> multiAgentNames() {
        return ConfigHelper.readAsList(MULTI_AGENT_NAMES.asNamed(prefix));
    }

    public String multiAgentIdSource() {
        return ConfigStore.getInstance()
                .get(MULTI_AGENT_ID_SOURCE.asNamed(prefix))
                .orElse(ROUTE_ID);
    }

    public String multiAgentIdSourceHeader() {
        return ConfigStore.getInstance()
                .get(MULTI_AGENT_ID_SOURCE_HEADER.asNamed(prefix))
                .orElse(null);
    }

    public String multiAgentIdSourceProperty() {
        return ConfigStore.getInstance()
                .get(MULTI_AGENT_ID_SOURCE_PROPERTY.asNamed(prefix))
                .orElse(null);
    }

    public String multiAgentIdSourceVariable() {
        return ConfigStore.getInstance()
                .get(MULTI_AGENT_ID_SOURCE_VARIABLE.asNamed(prefix))
                .orElse(null);
    }
}
