package io.kaoto.forage.integration.tests.suites;

import org.junit.platform.suite.api.AfterSuite;
import org.junit.platform.suite.api.BeforeSuite;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Suite
@SuiteDisplayName("springboot")
@SelectPackages("io.kaoto.forage")
public class SpringBootSuite {

    private static final Logger LOG = LoggerFactory.getLogger(SpringBootSuite.class);

    @AfterSuite
    public static void afterSuite() {
        TestSuiteHelper.afterSuite();
    }

    @BeforeSuite
    public static void beforeSuite() {
        TestSuiteHelper.beforeSuite("spring-boot", LOG);
    }
}
