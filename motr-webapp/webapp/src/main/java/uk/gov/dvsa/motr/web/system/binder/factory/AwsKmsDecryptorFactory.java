package uk.gov.dvsa.motr.web.system.binder.factory;

import uk.gov.dvsa.motr.config.Config;
import uk.gov.dvsa.motr.encryption.AwsKmsDecryptor;
import uk.gov.dvsa.motr.encryption.Decryptor;

import javax.inject.Inject;

import static com.amazonaws.regions.Region.getRegion;
import static com.amazonaws.regions.Regions.fromName;

import static uk.gov.dvsa.motr.web.system.SystemVariable.REGION;

public class AwsKmsDecryptorFactory implements BaseFactory<Decryptor> {

    private final Config config;

    @Inject
    public AwsKmsDecryptorFactory(Config config) {
        this.config = config;
    }

    @Override
    public Decryptor provide() {

        return new AwsKmsDecryptor(
                getRegion(
                        fromName(
                                config.getValue(REGION)
                        )
                )
        );
    }
}

