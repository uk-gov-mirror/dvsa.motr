package uk.gov.dvsa.motr.web.test.environment;

import org.junit.contrib.java.lang.system.EnvironmentVariables;

import uk.gov.dvsa.motr.web.system.SystemVariable;

public class TestEnvironmentVariables extends EnvironmentVariables {

    public TestEnvironmentVariables() {

        assetsUrl("http://url");
        assetsHash("981347823940237907edfdfdfdf");
        logLevel("INFO");
    }

    public TestEnvironmentVariables assetsUrl(String value) {
        return set(SystemVariable.STATIC_ASSETS_URL, value);
    }

    public TestEnvironmentVariables assetsHash(String value) {
        return set(SystemVariable.STATIC_ASSETS_HASH, value);
    }

    public TestEnvironmentVariables logLevel(String value) {
        return set(SystemVariable.LOG_LEVEL, value);
    }

    private TestEnvironmentVariables set(SystemVariable var, String value) {
        set(var.getName(), value);
        return this;
    }
}
