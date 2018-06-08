package uk.gov.dvsa.motr.notifier.helpers;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EuRoadworthinessToggleTest {

    private String euGoLiveDate = "2018-05-20";

    @Test
    public void testGetEuRoadworthinessReturnsTrue_WhenCurrentDateIsGreaterThanGoLiveDate() {
        String fakeCurrentDate = "2019-06-21";

        EuRoadworthinessToggle toggle = getFakeCurrentDateToggle(fakeCurrentDate);

        assertTrue(toggle.isEuRoadworthinessLive());
    }

    @Test
    public void testGetEuRoadworthinessReturnsFalse_WhenCurrentDateIsLessThanGoLiveDate() {

        String fakeCurrentDate = "2017-04-19";

        EuRoadworthinessToggle toggle = getFakeCurrentDateToggle(fakeCurrentDate);

        assertFalse(toggle.isEuRoadworthinessLive());
    }

    private EuRoadworthinessToggle getFakeCurrentDateToggle(String fakeCurrentDate) {
        return new EuRoadworthinessToggle(euGoLiveDate) {
            @Override public LocalDate getCurrentTime() {
                return LocalDate.parse(fakeCurrentDate);
            }
        };
    }
}
