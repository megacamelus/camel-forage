package org.apache.camel.forage.jms;

import jakarta.jms.ConnectionFactory;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import org.apache.camel.CamelContext;
import org.apache.camel.forage.core.annotations.ForageFactory;
import org.apache.camel.forage.core.common.BeanFactory;
import org.apache.camel.forage.core.common.ServiceLoaderHelper;
import org.apache.camel.forage.core.jms.ConnectionFactoryProvider;
import org.apache.camel.forage.core.jta.MandatoryJtaTransactionPolicy;
import org.apache.camel.forage.core.jta.NeverJtaTransactionPolicy;
import org.apache.camel.forage.core.jta.NotSupportedJtaTransactionPolicy;
import org.apache.camel.forage.core.jta.RequiredJtaTransactionPolicy;
import org.apache.camel.forage.core.jta.RequiresNewJtaTransactionPolicy;
import org.apache.camel.forage.core.jta.SupportsJtaTransactionPolicy;
import org.apache.camel.forage.core.util.config.ConfigStore;
import org.apache.camel.forage.jms.common.ConnectionFactoryCommonExportHelper;
import org.apache.camel.forage.jms.common.ConnectionFactoryConfig;
import org.apache.camel.forage.jms.common.ForageConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ForageFactory(
        value = "CamelConnectionFactoryFactory",
        components = {"camel-jms"},
        description = "Default ConnectionFactory factory with ServiceLoader discovery",
        factoryType = "ConnectionFactory",
        autowired = true)
public class ConnectionFactoryBeanFactory implements BeanFactory {
    private final Logger LOG = LoggerFactory.getLogger(ConnectionFactoryBeanFactory.class);

    private CamelContext camelContext;
    private static final String DEFAULT_CONNECTION_FACTORY = "connectionFactory";

    @Override
    public void configure() {

        ConnectionFactoryConfig config = new ConnectionFactoryConfig();
        Set<String> prefixes = ConfigStore.getInstance().readPrefixes(config, "(.+).jms\\..*");

        if (config.transactionEnabled()) {
            camelContext.getRegistry().bind("PROPAGATION_REQUIRED", new RequiredJtaTransactionPolicy());
            camelContext.getRegistry().bind("MANDATORY", new MandatoryJtaTransactionPolicy());
            camelContext.getRegistry().bind("NEVER", new NeverJtaTransactionPolicy());
            camelContext.getRegistry().bind("NOT_SUPPORTED", new NotSupportedJtaTransactionPolicy());
            camelContext.getRegistry().bind("REQUIRES_NEW", new RequiresNewJtaTransactionPolicy());
            camelContext.getRegistry().bind("SUPPORTS", new SupportsJtaTransactionPolicy());
        }

        if (!prefixes.isEmpty()) {
            for (String name : prefixes) {
                if (camelContext.getRegistry().lookupByNameAndType(name, ConnectionFactory.class) == null) {
                    ConnectionFactoryConfig cfConfig = new ConnectionFactoryConfig(name);
                    ForageConnectionFactory forageConnectionFactory = newConnectionFactory(cfConfig, name);
                    camelContext.getRegistry().bind(name, forageConnectionFactory.connectionFactory());
                }
            }
        } else {
            try {
                if (camelContext.getRegistry().lookupByNameAndType(DEFAULT_CONNECTION_FACTORY, ConnectionFactory.class)
                        == null) {
                    final List<ServiceLoader.Provider<ConnectionFactoryProvider>> providers =
                            findProviders(ConnectionFactoryProvider.class);
                    if (providers.size() == 1) {
                        ForageConnectionFactory forageConnectionFactory =
                                doCreateConnectionFactory(providers.get(0), null);
                        camelContext
                                .getRegistry()
                                .bind(DEFAULT_CONNECTION_FACTORY, forageConnectionFactory.connectionFactory());
                    } else {
                        throw new IllegalArgumentException(
                                "No ConnectionFactory implementation is present in the classpath");
                    }
                }
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    private synchronized ForageConnectionFactory newConnectionFactory(
            ConnectionFactoryConfig connectionFactoryConfig, String name) {
        final String connectionFactoryProviderClass =
                ConnectionFactoryCommonExportHelper.transformJmsKindIntoProviderClass(
                        connectionFactoryConfig.jmsKind());
        LOG.info("Creating ConnectionFactory of type {}", connectionFactoryProviderClass);

        final List<ServiceLoader.Provider<ConnectionFactoryProvider>> providers =
                findProviders(ConnectionFactoryProvider.class);

        final ServiceLoader.Provider<ConnectionFactoryProvider> connectionFactoryProvider =
                ServiceLoaderHelper.findProviderByClassName(providers, connectionFactoryProviderClass);

        if (connectionFactoryProvider == null) {
            LOG.warn("ConnectionFactory {} has no provider for {}", name, connectionFactoryProviderClass);
            return null;
        }

        return doCreateConnectionFactory(connectionFactoryProvider, name);
    }

    private ForageConnectionFactory doCreateConnectionFactory(
            ServiceLoader.Provider<ConnectionFactoryProvider> provider, String name) {
        final ConnectionFactoryProvider connectionFactoryProvider = provider.get();
        return new ForageConnectionFactory(connectionFactoryProvider.create(name));
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
