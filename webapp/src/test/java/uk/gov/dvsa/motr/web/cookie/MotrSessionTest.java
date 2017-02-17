package uk.gov.dvsa.motr.web.cookie;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MotrSessionTest {

    private static final String VRM = "VRZ";
    private static final String EMAIL = "test@test.com";
    private MotrSession motrSession;

    @Before
    public void setUp() {

        motrSession = new MotrSession();
    }

    @Test
    public void isAllowedOnPageReturnsTrueWhenVrmAndEmailSessionEntered() {

        motrSession.setEmail(EMAIL);
        motrSession.setVrm(VRM);
        boolean actual = motrSession.isAllowedOnPage();
        assertTrue(actual);
    }

    @Test
    public void isAllowedOnPageReturnsFalseWhenNoEmailSessionEntered() {

        motrSession.setVrm(VRM);
        boolean actual = motrSession.isAllowedOnPage();
        assertFalse(actual);
    }

    @Test
    public void isAllowedOnPageReturnsFalseWhenNoVrmSessionEntered() {

        motrSession.setEmail(EMAIL);
        boolean actual = motrSession.isAllowedOnPage();
        assertFalse(actual);
    }

    @Test
    public void isAllowedOnPageReturnsFalseWhenNoSessionEntered() {

        boolean actual = motrSession.isAllowedOnPage();
        assertFalse(actual);
    }

    @Test
    public void isAllowedOnEmailPageReturnsFalseWhenNoSessionEntered() {

        boolean actual = motrSession.isAllowedOnEmailPage();
        assertFalse(actual);
    }

    @Test
    public void isAllowedOnEmailPageReturnsTrueWhenVrmSessionEntered() {

        motrSession.setVrm(VRM);
        boolean actual = motrSession.isAllowedOnEmailPage();
        assertTrue(actual);
    }

    @Test
    public void getRegFromSessionReturnsRegWhenInSession() {

        motrSession.setVrm(VRM);
        String actual = motrSession.getRegNumberFromSession();
        assertEquals("VRZ", actual);
    }

    @Test
    public void getRegFromSessionReturnsEmptyStringWhenNoRegInSession() {

        String actual = motrSession.getRegNumberFromSession();
        assertEquals("", actual);
    }

    @Test
    public void getEmailFromSessionReturnsEmailWhenInSession() {

        motrSession.setEmail(EMAIL);
        String actual = motrSession.getEmailFromSession();
        assertEquals("test@test.com", actual);
    }

    @Test
    public void visitingFromReviewPageReturnsFalseWhenNotSet() {

        boolean actual = motrSession.visitingFromReviewPage();
        assertFalse(actual);
    }

    @Test
    public void visitingFromReviewPageReturnsTrueWhenSet() {

        motrSession.setVisitingFromReview(true);
        boolean actual = motrSession.visitingFromReviewPage();
        assertTrue(actual);
    }

    @Test
    public void getEmailFromSessionReturnsEmptyStringWhenNoEmailInSession() {

        String actual = motrSession.getEmailFromSession();
        assertEquals("", actual);
    }
}
