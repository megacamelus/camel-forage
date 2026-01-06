package io.kaoto.forage.agent.factory;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.model.chat.ChatModel;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.component.langchain4j.agent.api.Agent;
import org.apache.camel.component.langchain4j.agent.api.AgentConfiguration;
import org.apache.camel.component.langchain4j.agent.api.AgentFactory;
import io.kaoto.forage.core.ai.ChatMemoryBeanProvider;
import io.kaoto.forage.core.ai.ModelProvider;
import io.kaoto.forage.core.common.ServiceLoaderHelper;
import io.kaoto.forage.core.exceptions.RuntimeForageException;
import io.kaoto.forage.core.util.config.ConfigStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of AgentFactory that uses ServiceLoader to discover and create multiple agents
 */
public class MultiAgentFactory implements AgentFactory {
    private static final Logger LOG = LoggerFactory.getLogger(MultiAgentFactory.class);

    private CamelContext camelContext;
    private final MultiAgentConfig config = new MultiAgentConfig();

    private record AgentPair(AgentFactoryConfig agentFactoryConfig, Agent agent) {}

    private Map<String, AgentPair> agents = new ConcurrentHashMap<>();

    public MultiAgentFactory() {
        LOG.trace("Creating MultiAgentFactory");
    }

    @Override
    public void setCamelContext(CamelContext camelContext) {
        this.camelContext = camelContext;

        ConfigStore.getInstance().setClassLoader(camelContext.getApplicationContextClassLoader());
    }

    @Override
    public CamelContext getCamelContext() {
        return camelContext;
    }

    private List<ServiceLoader.Provider<ModelProvider>> findModelProviders() {
        ServiceLoader<ModelProvider> modelLoader =
                ServiceLoader.load(ModelProvider.class, camelContext.getApplicationContextClassLoader());

        return modelLoader.stream().toList();
    }

    private List<ServiceLoader.Provider<Agent>> findAgents() {
        ServiceLoader<Agent> serviceLoader =
                ServiceLoader.load(Agent.class, camelContext.getApplicationContextClassLoader());

        return serviceLoader.stream().toList();
    }

    private List<ServiceLoader.Provider<ChatMemoryBeanProvider>> findChatMemoryFactories() {
        ServiceLoader<ChatMemoryBeanProvider> serviceLoader =
                ServiceLoader.load(ChatMemoryBeanProvider.class, camelContext.getApplicationContextClassLoader());

        return serviceLoader.stream().toList();
    }

    public synchronized Agent createAgent(Exchange exchange, String agentId) throws Exception {
        if (LOG.isTraceEnabled()) {
            LOG.debug("Available agents: {}", agents);
        }

        if (agents.containsKey(agentId)) {
            LOG.debug("Reusing existing Agent for {}", agentId);
            final AgentPair agentPair = agents.get(agentId);
            return agentPair.agent;
        }

        final List<String> definedAgents = config.multiAgentNames();

        if (definedAgents.contains(agentId)) {
            LOG.info("Creating new Agent for {}", agentId);
            AgentFactoryConfig aFactoryConfig = new AgentFactoryConfig(agentId);

            LOG.info("Using factory {} for {}", aFactoryConfig.name(), agentId);

            Agent agent = newAgent(aFactoryConfig, agentId);

            LOG.info("Using agent {} for {}", agent, agentId);
            agents.put(agentId, new AgentPair(aFactoryConfig, agent));

            return agent;
        }

        throw AgentIdSelectorHelper.newUndefinedAgentException(config, exchange);
    }

    public synchronized Agent createAgent(Exchange exchange) throws Exception {
        final String agentId = AgentIdSelectorHelper.select(config, exchange);

        return createAgent(exchange, agentId);
    }

    private synchronized Agent newAgent(AgentFactoryConfig agentFactoryConfig, String name) {
        final String agentFactoryClass = agentFactoryConfig.providerAgentClass();
        LOG.info("Creating Agent of type {}", agentFactoryClass);

        final List<ServiceLoader.Provider<Agent>> providers = findAgents();

        final ServiceLoader.Provider<Agent> agentProvider =
                ServiceLoaderHelper.findProviderByClassName(providers, agentFactoryClass);

        if (agentProvider == null) {
            LOG.warn("Agent {} has no provider for {}", name, agentFactoryClass);
            return null;
        }

        return doCreateAgent(agentProvider, agentFactoryConfig);
    }

    private ModelProvider newModelProvider(AgentFactoryConfig agentFactoryConfig) {
        final String modelFactoryClass = agentFactoryConfig.providerModelFactoryClass();
        LOG.trace("Creating ModelProvider of type {}", modelFactoryClass);

        final List<ServiceLoader.Provider<ModelProvider>> providers = findModelProviders();
        final ServiceLoader.Provider<ModelProvider> modelProvider =
                ServiceLoaderHelper.findProviderByClassName(providers, modelFactoryClass);

        if (modelProvider == null) {
            return null;
        }

        return modelProvider.get();
    }

    private ChatMemoryBeanProvider newChatMemoryFactory(AgentFactoryConfig agentFactoryConfig) {
        final String chatFactoryClass = agentFactoryConfig.providerFeaturesMemoryFactoryClass();
        LOG.trace("Creating ChatMemoryFactory of type {}", chatFactoryClass);
        final List<ServiceLoader.Provider<ChatMemoryBeanProvider>> providers = findChatMemoryFactories();

        final ServiceLoader.Provider<ChatMemoryBeanProvider> chatMemoryFactoryProvider =
                ServiceLoaderHelper.findProviderByClassName(providers, chatFactoryClass);

        if (chatMemoryFactoryProvider == null) {
            return null;
        }

        return chatMemoryFactoryProvider.get();
    }

    private Agent doCreateAgent(ServiceLoader.Provider<Agent> provider, AgentFactoryConfig agentFactoryConfig) {
        final Agent agent = provider.get();

        if (agent instanceof ConfigurationAware configurationAware) {
            LOG.trace("Creating the model");
            ModelProvider modelProvider = newModelProvider(agentFactoryConfig);
            if (modelProvider == null) {
                throw new UnsupportedOperationException("A model must be provided for using an agent");
            }

            final ChatModel chatModel = modelProvider.create();
            final List<String> features = agentFactoryConfig.providerFeatures();

            ChatMemoryProvider chatMemoryProvider = null;
            if (features.contains(AgentFactoryConfigEntries.FEATURE_MEMORY)) {
                LOG.trace("Creating the agent memory ");
                final ChatMemoryBeanProvider chatMemoryBeanProvider = newChatMemoryFactory(agentFactoryConfig);
                if (chatMemoryBeanProvider != null) {
                    chatMemoryProvider = chatMemoryBeanProvider.create();
                }
            }

            AgentConfiguration agentConfiguration = new AgentConfiguration();
            agentConfiguration.withChatModel(chatModel).withChatMemoryProvider(chatMemoryProvider);

            final List<String> inputGuardrailsList = agentFactoryConfig.guardrailsInputClasses();
            setGuardrail(inputGuardrailsList, agentConfiguration::withInputGuardrailClasses);

            final List<String> outputGuardrailsList = agentFactoryConfig.guardrailsOutputClasses();
            setGuardrail(outputGuardrailsList, agentConfiguration::withOutputGuardrailClasses);

            configurationAware.configure(agentConfiguration);
        }

        return agent;
    }

    /**
     * The configuration comes as a list of classes in String format, but we need the list to be a list of Classes.
     * @param classesList
     * @param listConsumer
     */
    private void setGuardrail(List<String> classesList, Consumer<List<Class<?>>> listConsumer) {
        if (classesList != null && !classesList.isEmpty()) {

            final List<Class<?>> collect = classesList.stream()
                    .map(strClassName -> {
                        try {
                            final ClassLoader applicationContextClassLoader =
                                    camelContext.getApplicationContextClassLoader();
                            return Class.forName(strClassName, true, applicationContextClassLoader);
                        } catch (ClassNotFoundException e) {
                            final String className = strClassName == null ? "null" : strClassName;
                            throw new RuntimeForageException(
                                    String.format("The class named %s could not be loaded", className), e);
                        }
                    })
                    .collect(Collectors.toList());

            listConsumer.accept(collect);
        }
    }
}
