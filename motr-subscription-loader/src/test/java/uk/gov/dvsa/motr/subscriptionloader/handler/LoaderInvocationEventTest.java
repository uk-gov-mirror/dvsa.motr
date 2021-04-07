package uk.gov.dvsa.motr.subscriptionloader.handler;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoaderInvocationEventTest {

    private static final String TEST_TIME_STRING = "2011-01-01T12:12:12Z";

    @Test
    public void whenSettingTime_thenTimeIsCorrectlySet() {

        LoaderInvocationEvent event = new LoaderInvocationEvent();
        event.setTime(TEST_TIME_STRING);

        assertEquals(TEST_TIME_STRING, event.getTime());
        assertEquals(LocalDateTime.parse(TEST_TIME_STRING, DateTimeFormatter.ISO_DATE_TIME), event.getTimeAsDateTime());
    }

    @Test
    public void whenSettingPurgeFlagToFalse_flagIsSetToFalse() {

        LoaderInvocationEvent event = new LoaderInvocationEvent();
        event.setPurge(false);

        assertFalse(event.isPurge());
    }

    @Test
    public void whenPurgeFlagHasNotBeenSet_flagIsFalse() {

        LoaderInvocationEvent event = new LoaderInvocationEvent();

        assertFalse(event.isPurge());
    }
}
