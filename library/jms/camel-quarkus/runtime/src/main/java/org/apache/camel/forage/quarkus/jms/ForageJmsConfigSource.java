package org.apache.camel.forage.quarkus.jms;

import java.util.Set;
import org.apache.camel.forage.core.util.config.AbstractConfigSource;
import org.apache.camel.forage.core.util.config.ConfigHelper;
import org.apache.camel.forage.core.util.config.ConfigStore;
import org.apache.camel.forage.jms.common.ConnectionFactoryConfig;
import org.apache.camel.tooling.model.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForageJmsConfigSource extends AbstractConfigSource {
    private static final Logger LOG = LoggerFactory.getLogger(ForageJmsConfigSource.class);
    private static final ConfigurationBuilder builder = new ConfigurationBuilder();

    static {
        // try load named JMS properties
        ConnectionFactoryConfig config = new ConnectionFactoryConfig();
        Set<String> named = ConfigStore.getInstance().readPrefixes(config, ConfigHelper.getNamedPropertyRegexp("jms"));

        if (!named.isEmpty()) {
            for (String name : named) {
                ConnectionFactoryConfig connectionFactoryConfig = new ConnectionFactoryConfig(name);
                configureJms(name, connectionFactoryConfig);
            }
        } else if (!ConfigStore.getInstance()
                .readPrefixes(config, ConfigHelper.getDefaultPropertyRegexp("jms"))
                .isEmpty()) {
            configureJms("<default>", config);
        } else {
            LOG.trace("No jms config found.");
        }
    }

    private static void configureJms(String named, ConnectionFactoryConfig config) {

        // artemis
        String property = "quarkus.artemis.";
        if ("artemis".equals(config.jmsKind())) {
            if (!Strings.isNullOrEmpty(named)) {
                property += "\"" + named + "\".";
            }

            builder.add("quarkus.artemis.enabled", "true")
                    .add(property + "password", config.password())
                    .add(property + "username", config.username())
                    .add(property + "url", config.brokerUrl());

            // not supported
            if (Strings.isNullOrEmpty(config.clientId())) {
                LOG.warn("Client-id on Quarkus Artemis is not supported");
            }

            // pooled-jms is enabled by default (when library is present on the classpath)
            // configuration.put("quarkus.pooled-jms.pooling.enabled", "true");
            builder.add("quarkus.pooled-jms.max-connections", config::maxConnections);
            builder.add("quarkus.pooled-jms.max-sessions-per-connection", config::maxSessionsPerConnection);
            builder.addSecondsFromMillis("quarkus.pooled-jms.connection-idle-timeout", config::idleTimeoutMillis);
            builder.add("quarkus.pooled-jms.connection-check-interval", config::expiryTimeoutMillis);
            // connection-timeout -> it is not possible to set connection timeout on quarkus-artemis or
            // quarkus-pooled-jms
            builder.add("quarkus.pooled-jms.block-if-session-pool-is-full", config::blockIfFull);
            builder.addSecondsFromMillis(
                    "quarkus.pooled-jms.block-if-session-pool-is-full-timeout", config::blockIfFullTimeoutMillis);

            if (config.transactionEnabled()) {
                // based on https://docs.quarkiverse.io/quarkus-pooled-jms/dev/index.html#_xa_transaction_support and
                // https://docs.quarkiverse.io/quarkus-artemis/dev/quarkus-artemis-jms.html#_xa_capable_connection_factories
                builder.add("quarkus.pooled-jms.transaction", "xa");

                builder.add(
                        "quarkus.transaction-manager.default-transaction-timeout",
                        config.transactionTimeoutSeconds() + "S");
                builder.add("quarkus.transaction-manager.node-name", config::transactionNodeId);
                // Quarkus does not support transactionObjectStoreId
                builder.add(
                        "quarkus.transaction-manager.xa-resource-orphan-filters",
                        config::transactionXaResourceOrphanFilters);
                builder.add("quarkus.transaction-manager.recovery-modules", config::transactionRecoveryModules);
                builder.add("quarkus.transaction-manager.expiry-scanners", config::transactionExpiryScanners);
                builder.add(
                        "quarkus.transaction-manager.object-store.directory", config::transactionObjectStoreDirectory);
                builder.add("quarkus.transaction-manager.object-store.type", config::transactionObjectStoreType);
                builder.add("quarkus.transaction-manager.enable-recovery", config::transactionEnableRecovery);

                // Quarkus does not support transactionObjectStoreId
                builder.add("quarkus.transaction-manager.enable-recovery", config::transactionEnableRecovery);
                builder.add("quarkus.transaction-manager.recovery-modules", config::transactionRecoveryModules);
                builder.add(
                        "quarkus.transaction-manager.xa-resource-orphan-filters",
                        config::transactionXaResourceOrphanFilters);
                builder.add(
                        "quarkus.transaction-manager.object-store.directory", config::transactionObjectStoreDirectory);
            }
        } else if ("ibmmq".equals(config.jmsKind())) {
            // configuration is created via recorder, na property is required
        } else {
            throw new IllegalArgumentException(
                    "`%s` Jms kind is not supported by Quarkus runtime.".formatted(config.jmsKind()));
        }
    }

    @Override
    protected ConfigurationBuilder builder() {
        return builder;
    }
}
