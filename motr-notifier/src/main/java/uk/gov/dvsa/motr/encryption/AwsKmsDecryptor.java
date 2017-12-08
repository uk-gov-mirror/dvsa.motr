package uk.gov.dvsa.motr.encryption;

import com.amazonaws.regions.Region;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClient;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.DecryptResult;

import java.nio.ByteBuffer;
import java.util.Base64;

/**
 * Decryptor using AWS KMS service
 */
public class AwsKmsDecryptor implements Decryptor {

    private final Region region;

    public AwsKmsDecryptor(Region region) {

        this.region = region;
    }

    @Override
    public String decrypt(String encryptedValue) {

        AWSKMS kms = getKmsClient();
        kms.setRegion(region);

        ByteBuffer encryptedBlob = ByteBuffer.wrap(Base64.getDecoder().decode(encryptedValue));
        DecryptResult result = kms.decrypt(new DecryptRequest().withCiphertextBlob(encryptedBlob));

        return new String(result.getPlaintext().array());
    }

    AWSKMS getKmsClient() {
        return new AWSKMSClient();
    }
}
