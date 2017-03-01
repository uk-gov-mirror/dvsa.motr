package uk.gov.dvsa.motr.subscriptionloader.handler;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;

public class AwsCloudwatchEventTest {

    private static final String TEST_TIME_STRING = "2011-01-01T12:12:12Z";

    @Test
    public void whenSettingTime_thenTimeIsCorrectlySet() {
        AwsCloudwatchEvent event = new AwsCloudwatchEvent();
        event.setTime(TEST_TIME_STRING);

        assertEquals(TEST_TIME_STRING, event.getTime());
        assertEquals(LocalDateTime.parse(TEST_TIME_STRING, DateTimeFormatter.ISO_DATE_TIME), event.getTimeAsDateTime());
    }

}
