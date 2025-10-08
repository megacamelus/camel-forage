package org.apache.camel.forage.jms.artemis;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.XAConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQXAConnectionFactory;
import org.apache.camel.forage.core.annotations.ForageBean;
import org.apache.camel.forage.jms.common.ConnectionFactoryConfig;
import org.apache.camel.forage.jms.common.PooledConnectionFactory;

/**
 * ActiveMQ Artemis implementation extending PooledConnectionFactory.
 * Provides Artemis-specific connection factory configuration.
 */
@ForageBean(
        value = "artemis",
        components = {"camel-jms"},
        description = "ActiveMQ Artemis JMS ConnectionFactory Provider")
public class ArtemisJms extends PooledConnectionFactory {

    @Override
    protected ConnectionFactory createConnectionFactory(ConnectionFactoryConfig config) {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(config.brokerUrl());

        if (config.username() != null) {
            connectionFactory.setUser(config.username());
        }
        if (config.password() != null) {
            connectionFactory.setPassword(config.password());
        }
        if (config.clientId() != null) {
            connectionFactory.setClientID(config.clientId());
        }

        return (ConnectionFactory) connectionFactory;
    }

    @Override
    protected XAConnectionFactory createXAConnectionFactory(ConnectionFactoryConfig config) {
        ActiveMQXAConnectionFactory xaConnectionFactory = new ActiveMQXAConnectionFactory(config.brokerUrl());

        if (config.username() != null) {
            xaConnectionFactory.setUser(config.username());
        }
        if (config.password() != null) {
            xaConnectionFactory.setPassword(config.password());
        }
        if (config.clientId() != null) {
            xaConnectionFactory.setClientID(config.clientId());
        }

        return (XAConnectionFactory) xaConnectionFactory;
    }
}
