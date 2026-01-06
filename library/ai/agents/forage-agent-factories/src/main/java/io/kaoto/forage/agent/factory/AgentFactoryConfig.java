package io.kaoto.forage.agent.factory;

import static io.kaoto.forage.agent.factory.AgentFactoryConfigEntries.GUARDRAILS_INPUT_CLASSES;
import static io.kaoto.forage.agent.factory.AgentFactoryConfigEntries.GUARDRAILS_OUTPUT_CLASSES;
import static io.kaoto.forage.agent.factory.AgentFactoryConfigEntries.PROVIDER_AGENT_CLASS;
import static io.kaoto.forage.agent.factory.AgentFactoryConfigEntries.PROVIDER_FEATURES;
import static io.kaoto.forage.agent.factory.AgentFactoryConfigEntries.PROVIDER_FEATURES_MEMORY_FACTORY_CLASS;
import static io.kaoto.forage.agent.factory.AgentFactoryConfigEntries.PROVIDER_MODEL_FACTORY_CLASS;

import java.util.List;
import java.util.Optional;
import io.kaoto.forage.core.util.config.Config;
import io.kaoto.forage.core.util.config.ConfigHelper;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigStore;

/**
 * Configuration class for individual agent instances within the Camel Forage framework.
 *
 * <p>This class manages configuration settings for single agent implementations, providing
 * access to model providers, agent features, and memory factory configurations. It supports
 * both default and named/prefixed configurations for multi-instance scenarios.
 *
 * <p>This configuration is typically used by the MultiAgentFactory for agent setups,
 * supporting both single-agent and multi-agent scenarios.
 */
public class AgentFactoryConfig implements Config {

    private final String prefix;

    public AgentFactoryConfig() {
        this(null);
    }

    public AgentFactoryConfig(String prefix) {
        this.prefix = prefix;

        // First register new configuration modules. This happens only if a prefix is provided
        AgentFactoryConfigEntries.register(prefix);

        // Then, loads the configurations from the properties file associated with this Config module
        ConfigStore.getInstance().load(AgentFactoryConfig.class, this, this::register);

        // Lastly, load the overrides defined in system properties and environment variables
        AgentFactoryConfigEntries.loadOverrides(prefix);
    }

    @Override
    public void register(String name, String value) {
        Optional<ConfigModule> config = AgentFactoryConfigEntries.find(prefix, name);

        config.ifPresent(module -> ConfigStore.getInstance().set(module, value));
    }

    @Override
    public String name() {
        return "forage-agent-factory";
    }

    public String providerModelFactoryClass() {
        return ConfigStore.getInstance()
                .get(PROVIDER_MODEL_FACTORY_CLASS.asNamed(prefix))
                .orElse(null);
    }

    public List<String> providerFeatures() {
        return ConfigHelper.readAsList(PROVIDER_FEATURES.asNamed(prefix));
    }

    public String providerFeaturesMemoryFactoryClass() {
        return ConfigStore.getInstance()
                .get(PROVIDER_FEATURES_MEMORY_FACTORY_CLASS.asNamed(prefix))
                .orElse(null);
    }

    public String providerAgentClass() {
        return ConfigStore.getInstance()
                .get(PROVIDER_AGENT_CLASS.asNamed(prefix))
                .orElse(null);
    }

    public List<String> guardrailsInputClasses() {
        return ConfigHelper.readAsList(GUARDRAILS_INPUT_CLASSES.asNamed(prefix));
    }

    public List<String> guardrailsOutputClasses() {
        return ConfigHelper.readAsList(GUARDRAILS_OUTPUT_CLASSES.asNamed(prefix));
    }
}
