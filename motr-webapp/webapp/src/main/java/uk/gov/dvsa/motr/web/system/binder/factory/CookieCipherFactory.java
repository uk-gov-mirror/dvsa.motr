package uk.gov.dvsa.motr.web.system.binder.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.config.Config;
import uk.gov.dvsa.motr.config.ConfigKey;
import uk.gov.dvsa.motr.encryption.AesCipher;
import uk.gov.dvsa.motr.web.cookie.CookieCipher;
import uk.gov.dvsa.motr.web.helper.SystemVariableParam;
import uk.gov.dvsa.motr.web.system.SystemVariable;

import javax.inject.Inject;
import javax.inject.Provider;

import static uk.gov.dvsa.motr.web.system.SystemVariable.COOKIE_CIPHER_KEY;

public class CookieCipherFactory implements BaseFactory<AesCipher> {

    private final Config config;

    private static final Logger logger = LoggerFactory.getLogger(CookieCipherFactory.class);

    @Inject
    public CookieCipherFactory(Config config/*@SystemVariableParam(COOKIE_CIPHER_KEY) String cookieCipherKey*/) {
        logger.info("CookieCipherFactory - konstruktor");

        this.config = config;
    }

    @Override
    public CookieCipher provide() {
        logger.info("CookieCipherFactory - provide");

        return new CookieCipher(() -> config.getValue(SystemVariable.COOKIE_CIPHER_KEY));
    }
}

