package io.kaoto.forage.quarkus.jms;

import io.kaoto.forage.jms.ibmmq.IbmMqJms;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;
import jakarta.jms.ConnectionFactory;
import org.jboss.logging.Logger;

/**
 * Aggregation repository is created via Recorder
 */
@Recorder
public class ForageJmsRecorder {
    private static final Logger LOG = Logger.getLogger(ForageJmsRecorder.class);

    public RuntimeValue<ConnectionFactory> createIbmMQConnectionFactory(String id) {

        ConnectionFactory cf = new IbmMqJms().create(id);
        if (cf != null) {
            return new RuntimeValue<>(cf);
        }
        return null;
    }
}
