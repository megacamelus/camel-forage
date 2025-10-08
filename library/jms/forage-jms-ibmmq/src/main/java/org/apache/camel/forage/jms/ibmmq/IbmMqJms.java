package org.apache.camel.forage.jms.ibmmq;

import com.ibm.mq.jakarta.jms.MQConnectionFactory;
import com.ibm.mq.jakarta.jms.MQXAConnectionFactory;
import com.ibm.msg.client.jakarta.wmq.common.CommonConstants;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.XAConnectionFactory;
import org.apache.camel.forage.core.annotations.ForageBean;
import org.apache.camel.forage.jms.common.ConnectionFactoryConfig;
import org.apache.camel.forage.jms.common.PooledConnectionFactory;

/**
 * IBM MQ implementation extending PooledConnectionFactory.
 * Provides IBM MQ-specific connection factory configuration.
 */
@ForageBean(
        value = "ibmmq",
        components = {"camel-jms"},
        description = "IBM MQ JMS ConnectionFactory Provider")
public class IbmMqJms extends PooledConnectionFactory {

    @Override
    protected ConnectionFactory createConnectionFactory(ConnectionFactoryConfig config) {
        try {
            MQConnectionFactory connectionFactory = new MQConnectionFactory();
            configureConnectionFactory(connectionFactory, config);
            return connectionFactory;
        } catch (JMSException e) {
            throw new RuntimeException("Failed to create IBM MQ ConnectionFactory", e);
        }
    }

    @Override
    protected XAConnectionFactory createXAConnectionFactory(ConnectionFactoryConfig config) {
        try {
            MQXAConnectionFactory xaConnectionFactory = new MQXAConnectionFactory();
            configureConnectionFactory(xaConnectionFactory, config);
            return xaConnectionFactory;
        } catch (JMSException e) {
            throw new RuntimeException("Failed to create IBM MQ XAConnectionFactory", e);
        }
    }

    private void configureConnectionFactory(MQConnectionFactory connectionFactory, ConnectionFactoryConfig config)
            throws JMSException {
        // Parse broker URL to extract host, port, channel, and queue manager
        // Expected format: mq://host:port/channel/queueManager
        String brokerUrl = config.brokerUrl();
        String[] parts = parseBrokerUrl(brokerUrl);

        connectionFactory.setHostName(parts[0]);
        connectionFactory.setPort(Integer.parseInt(parts[1]));
        connectionFactory.setChannel(parts[2]);
        connectionFactory.setQueueManager(parts[3]);
        connectionFactory.setTransportType(CommonConstants.WMQ_CM_CLIENT);

        if (config.username() != null) {
            connectionFactory.setStringProperty(CommonConstants.USERID, config.username());
        }
        if (config.password() != null) {
            connectionFactory.setStringProperty(CommonConstants.PASSWORD, config.password());
        }
        if (config.clientId() != null) {
            connectionFactory.setClientID(config.clientId());
        }
    }

    private String[] parseBrokerUrl(String brokerUrl) {
        // Expected format: mq://host:port/channel/queueManager
        String url = brokerUrl.replaceFirst("^mq://", "");
        String[] parts = url.split("/");

        if (parts.length < 3) {
            throw new IllegalArgumentException(
                    "Invalid IBM MQ broker URL format. Expected: mq://host:port/channel/queueManager");
        }

        String[] hostPort = parts[0].split(":");
        String host = hostPort[0];
        String port = hostPort.length > 1 ? hostPort[1] : "1414";
        String channel = parts[1];
        String queueManager = parts[2];

        return new String[] {host, port, channel, queueManager};
    }
}
