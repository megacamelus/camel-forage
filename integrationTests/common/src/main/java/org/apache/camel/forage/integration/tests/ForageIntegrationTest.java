package org.apache.camel.forage.integration.tests;

import org.citrusframework.TestCaseRunner;
import org.citrusframework.actions.camel.CamelActionBuilder;

public interface ForageIntegrationTest {

    void runBeforeAll(TestCaseRunner runner, CamelActionBuilder camel);
}
