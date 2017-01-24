package uk.gov.dvsa.motr.web.config;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

import static java.util.Optional.empty;

public class EnvironmentVariableConfigTest {

    @Rule
    public final EnvironmentVariables environmentVariables  = new EnvironmentVariables();

    private final EnvironmentVariableConfig config = new EnvironmentVariableConfig();

    @Test
    public void valueIsReturnIfEnvironmentVariableIsProvided() {

        environmentVariables.set("varName", "varValue");

        assertEquals("Expected config value instead of empty", Optional.of("varValue"), config.getValue(() -> "varName"));
    }

    @Test
    public void emptyIsReturnIfEnvironmentVariableIsNotProvided() {

        assertEquals("Expected empty instead of config value", empty(), config.getValue(() -> "varName"));
    }
}
