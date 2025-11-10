package org.apache.camel.forage.integration.tests;

import org.apache.camel.forage.plugin.DataSourceExportHelper;
import org.citrusframework.DefaultTestCaseRunner;
import org.citrusframework.GherkinTestActionRunner;
import org.citrusframework.TestAction;
import org.citrusframework.TestActionBuilder;
import org.citrusframework.TestCase;
import org.citrusframework.actions.camel.CamelIntegrationRunCustomizedActionBuilder;
import org.citrusframework.context.TestContext;

/**
 * Custom test case runner takes care of the quarkus version used by citrus framework
 */
public class ForageTestCaseRunner extends DefaultTestCaseRunner {

    public ForageTestCaseRunner(TestContext context) {
        super(context);
    }

    public ForageTestCaseRunner(TestCase testCase, TestContext context) {
        super(testCase, context);
    }

    @Override
    public <T extends TestAction> GherkinTestActionRunner given(TestActionBuilder<T> builder) {
        if (builder instanceof CamelIntegrationRunCustomizedActionBuilder) {
            ((CamelIntegrationRunCustomizedActionBuilder<?, ?>) builder)
                    .withSystemProperty("camel.jbang.quarkusVersion", DataSourceExportHelper.getQuarkusVersion());
        }

        return super.given(builder);
    }
}
