package org.apache.camel.forage.integration.tests;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.citrusframework.TestActionBuilder;
import org.citrusframework.TestCaseRunner;
import org.citrusframework.actions.camel.CamelActionBuilder;
import org.citrusframework.junit.jupiter.CitrusExtensionHelper;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
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
public class IntegrationTestSetupExtension
        implements BeforeEachCallback, TestTemplateInvocationContextProvider, ParameterResolver {

    private final Logger LOG = LoggerFactory.getLogger(IntegrationTestSetupExtension.class);

    public static final String RUNTIME_PROPERTY = "integration_test_runtime+property";
    public static final String WORK_DIR = "target/.run";

    private boolean runBeforeAll = false;

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType() == ExtensionContext.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return extensionContext;
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        if (!runBeforeAll) {
            runBeforeAll = true;
            runBeforeAll(context);
        }
    }

    private void runBeforeAll(ExtensionContext context) {

        if (context.getRequiredTestInstance() instanceof ForageIntegrationTest) {

            TestCaseRunner runner = CitrusExtensionHelper.getTestRunner(context);
            CamelActionBuilder camel =
                    (CamelActionBuilder) TestActionBuilder.lookup("camel").get();

            LOG.info("Running 'runBeforeAll' setup for class: %s"
                    .formatted(context.getRequiredTestClass().getName()));
            ((ForageIntegrationTest) context.getRequiredTestInstance()).runBeforeAll(runner, camel);
        }
    }

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return true;
    }

    @Override
    public Stream provideTestTemplateInvocationContexts(ExtensionContext context) {
        return Arrays.stream(new TestTemplateInvocationContext[] {
            new RuntimeTestTemplateInvocationContext("<plain Camel>"),
            new RuntimeTestTemplateInvocationContext("--runtime=quarkus"),
            new RuntimeTestTemplateInvocationContext("--runtime=spring-boot")
        });
    }

    static final class RuntimeTestTemplateInvocationContext implements TestTemplateInvocationContext {
        private final Logger LOG = LoggerFactory.getLogger(RuntimeTestTemplateInvocationContext.class);
        private final String runtime;

        public RuntimeTestTemplateInvocationContext(String runtime) {
            this.runtime = runtime;
        }

        @Override
        public String getDisplayName(int invocationIndex) {
            return runtime;
        }

        @Override
        public List<Extension> getAdditionalExtensions() {
            return Arrays.asList(
                    (BeforeTestExecutionCallback) context -> {
                        logText(getDisplayName(0));
                        if (runtime.startsWith("--")) {
                            System.setProperty(RUNTIME_PROPERTY, runtime);
                        }
                    },
                    (AfterTestExecutionCallback) context -> {
                        System.clearProperty(RUNTIME_PROPERTY);
                    });
        }

        private void logText(String text) {

            int totalLength = 80;
            String paddedText = " " + text + " ";
            int textLength = paddedText.length();

            int dashLength = totalLength - textLength;
            int leftDashes = dashLength / 2;
            int rightDashes = dashLength - leftDashes;

            StringBuilder line = new StringBuilder();
            line.append("-".repeat(leftDashes));
            line.append(paddedText);
            line.append("-".repeat(rightDashes));

            LOG.info("-".repeat(totalLength));
            LOG.info(line.toString());
            LOG.info("-".repeat(totalLength));
        }
    }
}
