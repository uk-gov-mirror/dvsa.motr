package uk.gov.dvsa.motr.web.config;

import org.junit.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class CachedConfigTest {

    @Test
    public void configIsCalculatedOnlyOnceWhenCached() {

        final AtomicInteger noOfCalculations = new AtomicInteger(0);
        Config targetConfig = x -> {
            noOfCalculations.incrementAndGet();
            return Optional.of("value");
        };
        ConfigKey testKey = () -> "configKey";

        Config cachedConfig = new CachedConfig(targetConfig);

        cachedConfig.getValue(testKey);
        cachedConfig.getValue(testKey);

        assertEquals("There was more than 1 calculations of config", 1, noOfCalculations.get());
    }
}
