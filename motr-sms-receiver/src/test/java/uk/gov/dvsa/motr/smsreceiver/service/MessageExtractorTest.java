package uk.gov.dvsa.motr.smsreceiver.service;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;


public class MessageExtractorTest {

    public static final String A_STRING_WITHOUT_S_T_OP = "A string without s.t op";
    public static final String STOP = "STOP";
    public static final String STOP_WITH_VRM = "STOP SOMETHING";
    public static final String VRM = "SOMETHING";
    public static final String MOBILE_NUMBER_WITH_PREFIX = "4477031212";
    public static final String MOBILE_NUMBER_PREFIX_REPLACED = "077031212";
    public static final String MOBILE_NUMBER = "07703124412";

    private MessageExtractor messageExtractor = new MessageExtractor();

    @Test
    public void whenNoStopPresent_ThenEmptyReturned() {

        String vrm = messageExtractor.getVrmFromMesageBody(A_STRING_WITHOUT_S_T_OP);
        assertSame("", vrm);
    }

    @Test
    public void whenOnlyStopPresent_ThenEmptyReturned() {

        String vrm = messageExtractor.getVrmFromMesageBody(STOP);
        assertSame("", vrm);
    }

    @Test
    public void whenStopAndVrmPresent_ThenVrmReturned() {

        String vrm = messageExtractor.getVrmFromMesageBody(STOP_WITH_VRM);
        assertEquals(VRM, vrm);
    }

    @Test
    public void whenNumberHasInternationalPrefix_ThenItIsStrippedOff() {

        String mobileNumber = messageExtractor.getMobileNumberWithoutInternationalCode(MOBILE_NUMBER_WITH_PREFIX);
        assertEquals(MOBILE_NUMBER_PREFIX_REPLACED, mobileNumber);
    }

    @Test
    public void whenNumberIsInCorrectFormat_ThenNoChange() {

        String mobileNumber = messageExtractor.getMobileNumberWithoutInternationalCode(MOBILE_NUMBER);
        assertEquals(MOBILE_NUMBER, mobileNumber);
    }
}
