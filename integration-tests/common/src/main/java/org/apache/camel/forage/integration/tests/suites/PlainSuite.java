package org.apache.camel.forage.integration.tests.suites;

import org.junit.platform.suite.api.AfterSuite;
import org.junit.platform.suite.api.BeforeSuite;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Suite
@SuiteDisplayName("<plain>")
@SelectPackages("org.apache.camel.forage")
public class PlainSuite {

    private static final Logger LOG = LoggerFactory.getLogger(PlainSuite.class);

    @AfterSuite
    public static void afterSuite() {
        TestSuiteHelper.afterSuite();
    }

    @BeforeSuite
    public static void beforeSuite() {
        TestSuiteHelper.beforeSuite("<plain>", LOG);
    }
}
