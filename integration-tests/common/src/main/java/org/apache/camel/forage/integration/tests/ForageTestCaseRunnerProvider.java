package org.apache.camel.forage.integration.tests;

import org.citrusframework.TestCase;
import org.citrusframework.TestCaseRunnerProvider;
import org.citrusframework.context.TestContext;

/**
 * Test case runner provider instantiates custom test case runner and is registered via
 * META-INF/citrus/test/runner file.
 */
public class ForageTestCaseRunnerProvider implements TestCaseRunnerProvider {

    @Override
    public ForageTestCaseRunner createTestCaseRunner(TestContext context) {
        return new ForageTestCaseRunner(context);
    }

    @Override
    public ForageTestCaseRunner createTestCaseRunner(TestCase testCase, TestContext context) {
        return new ForageTestCaseRunner(testCase, context);
    }
}
