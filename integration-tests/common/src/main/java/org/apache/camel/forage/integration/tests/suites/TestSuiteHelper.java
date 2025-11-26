package org.apache.camel.forage.integration.tests.suites;

import org.apache.camel.forage.integration.tests.IntegrationTestSetupExtension;
import org.apache.camel.forage.plugin.ExportHelper;
import org.slf4j.Logger;

/**
 * Helper class for Suites.
 */
class TestSuiteHelper {

    public static void afterSuite() {
        System.clearProperty(IntegrationTestSetupExtension.RUNTIME_PROPERTY);
        System.clearProperty("citrus.camel.jbang.version");
    }

    public static void beforeSuite(String runtimeValue, Logger log) {
        if (!runtimeValue.startsWith("<")) {
            System.setProperty(IntegrationTestSetupExtension.RUNTIME_PROPERTY, runtimeValue);
        } else {
            System.clearProperty(IntegrationTestSetupExtension.RUNTIME_PROPERTY);
        }
        System.setProperty("citrus.camel.jbang.version", ExportHelper.getCamelVersion());
    }
}
