package uk.gov.dvsa.motr.smsreceiver.system.binder;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import uk.gov.dvsa.motr.smsreceiver.config.Config;
import uk.gov.dvsa.motr.smsreceiver.encryption.Decryptor;
import uk.gov.dvsa.motr.smsreceiver.system.binder.factory.AwsKmsDecryptorFactory;
import uk.gov.dvsa.motr.smsreceiver.system.binder.factory.MotrConfigFactory;

import javax.inject.Singleton;

public class ConfigBinder extends AbstractBinder {

    @Override
    protected void configure() {

        bindFactory(MotrConfigFactory.class).to(Config.class).in(Singleton.class);
        bindFactory(AwsKmsDecryptorFactory.class).to(Decryptor.class);
    }
}
