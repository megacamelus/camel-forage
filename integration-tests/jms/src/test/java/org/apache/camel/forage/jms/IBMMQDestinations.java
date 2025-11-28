package org.apache.camel.forage.jms;

import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.headers.MQDataException;
import com.ibm.mq.headers.pcf.PCFMessage;
import com.ibm.mq.headers.pcf.PCFMessageAgent;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/**
 * IBM MQ queries have to be created before starting the routes.
 */
public class IBMMQDestinations {
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASSWORD = "passw0rd";
    private static final String ADMIN_CHANNEL = "DEV.ADMIN.SVRCONN";

    private final String host;
    private final int port;
    private final String queueManagerName;

    private final PCFMessageAgent agent;
    // The destination can be created only once, otherwise the request will fail
    private final Set<String> createdQueues = new HashSet<>();

    public IBMMQDestinations(String host, int port, String queueManagerName) {
        this.host = host;
        this.port = port;
        this.queueManagerName = queueManagerName;

        // Disable creating log files for client
        System.setProperty("com.ibm.msg.client.commonservices.log.status", "OFF");

        agent = createPCFAgent();
    }

    private MQQueueManager createQueueManager() {
        Hashtable<String, Object> properties = new Hashtable<>();
        properties.put(MQConstants.HOST_NAME_PROPERTY, host);
        properties.put(MQConstants.PORT_PROPERTY, port);
        properties.put(MQConstants.CHANNEL_PROPERTY, ADMIN_CHANNEL);
        properties.put(MQConstants.USE_MQCSP_AUTHENTICATION_PROPERTY, true);
        properties.put(MQConstants.USER_ID_PROPERTY, ADMIN_USER);
        properties.put(MQConstants.PASSWORD_PROPERTY, ADMIN_PASSWORD);
        try {
            return new MQQueueManager(queueManagerName, properties);
        } catch (MQException e) {
            throw new RuntimeException("Unable to create MQQueueManager:", e);
        }
    }

    private PCFMessageAgent createPCFAgent() {
        try {
            return new PCFMessageAgent(createQueueManager());
        } catch (MQDataException e) {
            throw new RuntimeException("Unable to create PCFMessageAgent:", e);
        }
    }

    private void sendRequest(PCFMessage request) {
        try {
            agent.send(request);
        } catch (Exception e) {
            throw new RuntimeException("Unable to send PCFMessage:", e);
        }
    }

    public void createQueue(String queueName) {
        if (!createdQueues.contains(queueName)) {
            PCFMessage request = new PCFMessage(MQConstants.MQCMD_CREATE_Q);
            request.addParameter(MQConstants.MQCA_Q_NAME, queueName);
            request.addParameter(MQConstants.MQIA_Q_TYPE, MQConstants.MQQT_LOCAL);
            sendRequest(request);
            createdQueues.add(queueName);
        }
    }
}
