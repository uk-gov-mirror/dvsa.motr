package uk.gov.dvsa.motr.web.resource;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.formatting.PhoneNumberFormatter;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;
import uk.gov.dvsa.motr.web.validator.PhoneNumberValidator;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(DataProviderRunner.class)
public class PhoneNumberResourceTest {

    private MotrSession motrSession;
    private TemplateEngineStub engine;
    private PhoneNumberResource resource;
    private PhoneNumberValidator validator;

    @Before
    public void setup() {

        validator = mock(PhoneNumberValidator.class);
        motrSession = mock(MotrSession.class);
        engine = new TemplateEngineStub();
        resource = new PhoneNumberResource(motrSession, engine, validator);
    }

    @Test
    public void phoneNumberTemplateIsRenderedOnGet() throws Exception {

        when(motrSession.isAllowedOnPhoneNumberEntryPage()).thenReturn(true);

        assertEquals(200, resource.phoneNumberPageGet().getStatus());
        assertEquals("phone-number", engine.getTemplate());
    }

    @Test
    @UseDataProvider("dataProviderValidPhoneNumber")
    public void onPostWithValid_ThenRedirectedToReviewPage(String phoneNumber) throws Exception {

        when(validator.isValid(PhoneNumberFormatter.trimWhitespace(phoneNumber))).thenReturn(true);
        Response response = resource.phoneNumberPagePost(phoneNumber);

        assertEquals(302, response.getStatus());
    }

    @DataProvider
    public static Object[][] dataProviderValidPhoneNumber() {

        return new Object[][] {
                { "07123456789" },
                { "+44 7123456789" },
                { "44 7123456789" },
                { "0044 7123456789" },
                { "(44)7123 456 789" },
                { "(+44)7123 456 789" },
        };
    }

    @Test
    @UseDataProvider("dataProviderInvalidPhoneNumber")
    public void onPostWithInValid_ThenNotRedirectedToReviewPage(String phoneNumber) throws Exception {

        when(validator.isValid(PhoneNumberFormatter.trimWhitespace(phoneNumber))).thenReturn(false);
        Response response = resource.phoneNumberPagePost(phoneNumber);

        assertEquals(200, response.getStatus());
        assertEquals("phone-number", engine.getTemplate());
    }

    @DataProvider
    public static Object[][] dataProviderInvalidPhoneNumber() {

        return new Object[][] {
                { "712345678910" },
                { "+44 (0)7123 456 789 10" },
                { "004471234567" },
                { "0712345678" },
                { "+44 8081 570364" },
                { "020 7946 0991" },
                { " " },
                { "07123 ☟☜⬇⬆☞☝" },
                { "ALPHANUM3R1C" },
                { "07123 456789..." },
                { "071a234b5678c" },
                { "07 12.34.56 78" },
        };
    }
}
