package org.apache.camel.forage.integration.tests;

import java.util.ArrayList;
import java.util.Map;
import org.apache.camel.forage.plugin.DataSourceExportHelper;
import org.citrusframework.TestActionBuilder;
import org.citrusframework.TestCaseRunner;
import org.citrusframework.camel.actions.CamelActionBuilder;
import org.citrusframework.context.TestContext;
import org.citrusframework.junit.jupiter.CitrusExtensionHelper;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JUni5 extension, responsible for:
 * <p/>
 * <ul>
 * <li>Copying the test resource files into working directory.</li>
 * </ul>
 * <li>Starting tests for all runtimes. This is implemented by {@link org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider}.
 * Each run puts value into system properties with key {@link IntegrationTestSetupExtension#RUNTIME_PROPERTY}.
 * There are 3 values: null, "--runtime=spring-boot" and "--runtime=quarkus".</li>
 * <p/>
 * Test class should be annotated with
 * <ul>
 *     <li>@CitrusSupport</li>
 *     <li>@Testcontainers</li>
 *     <li>@ExtendWith(IntegrationTestSetupExtension.class)</li>
 * </ul>
 * and should add args to the citrus runner similar to <pre>.withArg(System.getProperty(IntegrationTestSetupExtension.RUNTIME_PROPERTY))</pre>
 */
public class IntegrationTestSetupExtension implements BeforeEachCallback, AfterAllCallback {

    private final Logger LOG = LoggerFactory.getLogger(IntegrationTestSetupExtension.class);

    public static final String RUNTIME_PROPERTY = "INTEGRATION_TEST_RUNTIME";

    private boolean runBeforeAll = false;
    private Map<String, Object> variables;
    private final ArrayList<AutoCloseable> closeables = new ArrayList<>();
    private TestContext previousTestContext;

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        if (!runBeforeAll) {
            runBeforeAll = true;
            internalBeforeAll(context);
            runBeforeAll(context);
        }
        // save test context variables
        TestContext testContext = CitrusExtensionHelper.getTestContext(context);
        if (previousTestContext != null) {
            testContext.getVariables().putAll(previousTestContext.getVariables());
        } else {
            previousTestContext = testContext;
        }
    }

    private void runBeforeAll(ExtensionContext context) {

        if (context.getRequiredTestInstance() instanceof ForageIntegrationTest) {

            TestCaseRunner runner = CitrusExtensionHelper.getTestRunner(context);

            LOG.info("Running 'runBeforeAll' setup for class: %s"
                    .formatted(context.getRequiredTestClass().getName()));
            ((ForageIntegrationTest) context.getRequiredTestInstance()).runBeforeAll(runner, closeables::add);
        }
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        previousTestContext = null;
        LOG.info("Running 'afterAll' setup for class: %s"
                .formatted(context.getRequiredTestClass().getName()));
        closeables.forEach(c -> {
            try {
                c.close();
            } catch (Exception e) {
                LOG.warn("Error closing test case", e);
            }
        });
        closeables.clear();
    }

    private void internalBeforeAll(ExtensionContext context) throws Exception {
        String projectVersion = DataSourceExportHelper.getProjectVersion();
        TestCaseRunner runner = CitrusExtensionHelper.getTestRunner(context);
        CamelActionBuilder camel =
                (CamelActionBuilder) TestActionBuilder.lookup("camel").get();
        // ensure, that forage plugin is installed
        CitrusExtensionHelper.getTestRunner(context)
                .when(camel.jbang()
                        .plugin()
                        .add()
                        .pluginName("forage")
                        .withArg("--artifactId", "camel-jbang-plugin-forage")
                        .withArg("--groupId", "org.apache.camel.forage")
                        .withArg("--version", projectVersion)
                        .withArg("--gav", "org.apache.camel.forage:camel-jbang-plugin-forage:" + projectVersion));
    }
}
