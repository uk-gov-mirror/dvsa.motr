package uk.gov.dvsa.motr.web.system.binder.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.encryption.AesCipher;
import uk.gov.dvsa.motr.web.cookie.CookieCipher;
import uk.gov.dvsa.motr.web.helper.SystemVariableParam;

import javax.inject.Inject;

import static uk.gov.dvsa.motr.web.system.SystemVariable.COOKIE_CIPHER_KEY;

public class CookieCipherFactory implements BaseFactory<AesCipher> {

    private final String cookieCipherKey;

    private static final Logger logger = LoggerFactory.getLogger(CookieCipherFactory.class);

    @Inject
    public CookieCipherFactory(@SystemVariableParam(COOKIE_CIPHER_KEY) String cookieCipherKey) {
        logger.info("CookieCipherFactory - konstruktor");

        this.cookieCipherKey = cookieCipherKey;
    }

    @Override
    public CookieCipher provide() {
        logger.info("CookieCipherFactory - provide");

        return new CookieCipher(cookieCipherKey);
    }
}

