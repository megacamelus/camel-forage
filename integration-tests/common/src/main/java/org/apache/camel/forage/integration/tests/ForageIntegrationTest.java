package org.apache.camel.forage.integration.tests;

import java.util.function.Consumer;
import org.citrusframework.TestCaseRunner;

/**
 * Interface required for special test cases:
 * <p/>
 * <ul>
 *     <li>Start routes once per class - implement {@link #runBeforeAll}</li>. See example {@link org.apache.camel.forage.jdbc.MultiTest}.
 * </ul>
 * <p/>
 * The test class has to register extension like <pre>@ExtendWith(IntegrationTestSetupExtension.class)</pre>
 *
 */
public interface ForageIntegrationTest {

    /**
     * Start routes once for the class lifetime.
     * Don't forget to register a cleanup method via @{link java.lang.AutoCloseable}
     */
    void runBeforeAll(TestCaseRunner runner, Consumer<AutoCloseable> afterAll);
}
