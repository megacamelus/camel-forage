package io.kaoto.forage.quarkus.jms.deployment;

import io.kaoto.forage.core.annotations.FactoryType;
import io.kaoto.forage.core.annotations.FactoryVariant;
import io.kaoto.forage.core.annotations.ForageFactory;
import io.kaoto.forage.core.util.config.ConfigHelper;
import io.kaoto.forage.core.util.config.ConfigStore;
import io.kaoto.forage.jms.common.ConnectionFactoryConfig;
import io.kaoto.forage.quarkus.jms.ForageJmsRecorder;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.runtime.RuntimeValue;
import jakarta.jms.ConnectionFactory;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.camel.quarkus.core.deployment.spi.CamelRuntimeBeanBuildItem;
import org.jboss.logging.Logger;

@ForageFactory(
        value = "JMS Connection (Quarkus)",
        components = {"camel-jms"},
        description = "Native JMS ConnectionFactory for Quarkus with compile-time optimization",
        type = FactoryType.CONNECTION_FACTORY,
        autowired = true,
        configClass = ConnectionFactoryConfig.class,
        variant = FactoryVariant.QUARKUS)
public class ForageJmsProcessor {

    private static final Logger LOG = Logger.getLogger(ForageJmsProcessor.class);
    private static final String FEATURE = "forage-jms";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    @Record(value = ExecutionTime.STATIC_INIT)
    void registerIbmMqConnectionFactory(ForageJmsRecorder recorder, BuildProducer<CamelRuntimeBeanBuildItem> beans)
            throws Exception {

        ConnectionFactoryConfig config = new ConnectionFactoryConfig();
        Set<String> named = ConfigStore.getInstance().readPrefixes(config, ConfigHelper.getNamedPropertyRegexp("jms"));

        Map<String, ConnectionFactoryConfig> configs = named.isEmpty()
                ? Collections.singletonMap((String) null, config)
                : named.stream().collect(Collectors.toMap(n -> n, ConnectionFactoryConfig::new));

        for (Map.Entry<String, ConnectionFactoryConfig> entry : configs.entrySet()) {
            if ("ibmmq".equals(entry.getValue().jmsKind())) {
                LOG.info("Recording IBMMMQ connection factory for url: "
                        + entry.getValue().brokerUrl());
                // create connection factory
                RuntimeValue<ConnectionFactory> connectionFactory =
                        recorder.createIbmMQConnectionFactory(entry.getKey());
                if (connectionFactory != null) {
                    beans.produce(new CamelRuntimeBeanBuildItem(
                            Optional.ofNullable(entry.getValue().name()).orElse(entry.getKey()),
                            ConnectionFactory.class.getName(),
                            connectionFactory));
                }
            }
        }
    }
}
