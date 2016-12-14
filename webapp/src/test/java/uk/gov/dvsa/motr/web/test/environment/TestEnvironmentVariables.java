package uk.gov.dvsa.motr.web.test.environment;

import org.junit.contrib.java.lang.system.EnvironmentVariables;

import uk.gov.dvsa.motr.web.system.SystemVariable;

public class TestEnvironmentVariables extends EnvironmentVariables {

    public TestEnvironmentVariables() {
        assetsUrl("http://url");
    }

    public TestEnvironmentVariables assetsUrl(String value) {
        return set(SystemVariable.STATIC_ASSETS_URL, value);
    }

    private TestEnvironmentVariables set(SystemVariable var, String value) {
        set(var.getName(), value);
        return this;
    }
}
