package uk.gov.dvsa.motr.web.system.binder.factory;

import uk.gov.dvsa.motr.encryption.AesCipher;
import uk.gov.dvsa.motr.web.cookie.CookieCipher;
import uk.gov.dvsa.motr.web.helper.SystemVariableParam;

import javax.inject.Inject;

import static uk.gov.dvsa.motr.web.system.SystemVariable.COOKIE_CIPHER_KEY;

public class CookieCipherFactory implements BaseFactory<CookieCipher> {

    private final String cookieCipherKey;

    @Inject
    public CookieCipherFactory(@SystemVariableParam(COOKIE_CIPHER_KEY) String cookieCipherKey) {

        this.cookieCipherKey = cookieCipherKey;
    }

    @Override
    public CookieCipher provide() {

        AesCipher aesCipher = new AesCipher(cookieCipherKey);
        return new CookieCipher(aesCipher);
    }
}

