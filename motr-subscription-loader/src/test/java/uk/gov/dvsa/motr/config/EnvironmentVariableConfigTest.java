package uk.gov.dvsa.motr.config;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import static org.junit.Assert.assertEquals;

public class EnvironmentVariableConfigTest {

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    private final EnvironmentVariableConfig config = new EnvironmentVariableConfig();

    @Test
    public void valueIsReturnIfEnvironmentVariableIsProvided() {

        environmentVariables.set("varName", "varValue");

        assertEquals("Expected config value instead of empty", "varValue", config.getValue(() -> "varName"));
    }

    @Test(expected = RuntimeException.class)
    public void emptyIsReturnIfEnvironmentVariableIsNotProvided() {

        config.getValue(() -> "varName");
    }

}
