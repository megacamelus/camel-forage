package org.apache.camel.forage.integration.tests.suites;

import org.apache.camel.forage.integration.tests.IntegrationTestSetupExtension;
import org.apache.camel.forage.plugin.DataSourceExportHelper;
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
            System.setProperty(IntegrationTestSetupExtension.RUNTIME_PROPERTY, "--runtime=" + runtimeValue);
        } else {
            System.clearProperty(IntegrationTestSetupExtension.RUNTIME_PROPERTY);
        }
        System.setProperty("citrus.camel.jbang.version", DataSourceExportHelper.getCamelVersion());
        logText(runtimeValue, log);
    }

    private static void logText(String text, Logger log) {

        int totalLength = 80;

        if (log != null) {
            String paddedText = " " + text + " ";
            int textLength = paddedText.length();

            int dashLength = totalLength - textLength;
            int leftDashes = dashLength / 2;
            int rightDashes = dashLength - leftDashes;

            StringBuilder line = new StringBuilder();
            line.append("-".repeat(leftDashes));
            line.append(paddedText);
            line.append("-".repeat(rightDashes));

            log.info("-".repeat(totalLength));
            log.info(line.toString());
            log.info("-".repeat(totalLength));
        }
    }
}
