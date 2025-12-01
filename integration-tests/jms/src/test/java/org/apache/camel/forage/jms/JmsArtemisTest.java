package org.apache.camel.forage.jms;

import java.util.Collections;
import java.util.function.Consumer;
import org.apache.camel.forage.integration.tests.ForageIntegrationTest;
import org.apache.camel.forage.integration.tests.ForageTestCaseRunner;
import org.apache.camel.forage.integration.tests.IntegrationTestSetupExtension;
import org.citrusframework.annotations.CitrusTest;
import org.citrusframework.junit.jupiter.CitrusSupport;
import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.activemq.ArtemisContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@CitrusSupport
@Testcontainers
@ExtendWith(IntegrationTestSetupExtension.class)
public class JmsArtemisTest implements ForageIntegrationTest {
    private static final Logger LOG = LoggerFactory.getLogger(JmsArtemisTest.class);

    static final String ARTEMIS_IMAGE_NAME =
            ConfigProvider.getConfig().getValue("activemq.artemis.container.image", String.class);
    public static final String INTEGRATION_NAME = "jms-routes";

    @Container
    static ArtemisContainer artemis = new ArtemisContainer(
                    DockerImageName.parse(ARTEMIS_IMAGE_NAME).asCompatibleSubstituteFor("apache/activemq-artemis"))
            .withExposedPorts(61616, 8161)
            .withUser("artemis")
            .withPassword("artemis");

    @Override
    public String runBeforeAll(ForageTestCaseRunner runner, Consumer<AutoCloseable> afterAll) {
        // running jbang forage run with required resources and required runtime
        runner.when(forageRun(INTEGRATION_NAME, "forage-connectionfactory.properties", "route-artemis.camel.yaml")
                .dumpIntegrationOutput(true)
                .withEnvs(Collections.singletonMap(
                        "JMS_BROKER_URL", "tcp://" + artemis.getHost() + ":" + artemis.getMappedPort(61616))));

        return INTEGRATION_NAME;
    }

    /**
     * Test based on <a href="https://github.com/megacamelus/forage-examples/tree/main/jms/transactional">JMS transactional example</a>.
     */
    @Test
    @CitrusTest()
    public void artemisTransactional(ForageTestCaseRunner runner) {

        // validation of logged message
        runner.then(camel().jbang()
                .verify()
                .integration(INTEGRATION_NAME)
                .waitForLogMessage("Successfully processed message: Transactional message"));
    }
}
