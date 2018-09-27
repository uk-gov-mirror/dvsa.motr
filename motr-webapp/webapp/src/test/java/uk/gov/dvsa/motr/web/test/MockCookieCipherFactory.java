package uk.gov.dvsa.motr.web.test;

import uk.gov.dvsa.motr.encryption.AesCipher;
import uk.gov.dvsa.motr.web.cookie.CookieCipher;
import uk.gov.dvsa.motr.web.system.binder.factory.BaseFactory;

import static org.mockito.Mockito.mock;

public class MockCookieCipherFactory implements BaseFactory<CookieCipher> {

    @Override
    public CookieCipher provide() {

        AesCipher aesCipher = mock(AesCipher.class);
        return new CookieCipher(aesCipher);
    }
}
