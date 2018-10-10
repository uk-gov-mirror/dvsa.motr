package uk.gov.dvsa.motr.encryption;

import com.amazonaws.regions.Region;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.DecryptResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Base64;

/**
 * Decryptor using AWS KMS service
 */
public class AwsKmsDecryptor implements Decryptor {

    private static final Logger logger = LoggerFactory.getLogger(AwsKmsDecryptor.class);

    private static final String PLAINTEXT_SECRET_PREFIX = "PLAINTEXT_";

    private final Region region;

    public AwsKmsDecryptor(Region region) {

        this.region = region;
    }

    @Override
    public String decrypt(String encryptedValue) {

        logger.info("AwsKmsDecryptor - decrypt");

        if (!isEncrypted(encryptedValue)) {
            logger.info("Value {} marked as plain-text. Not decrypting.", encryptedValue);
            return encryptedValue.replace(PLAINTEXT_SECRET_PREFIX, "");
        }
        logger.info("AwsKmsDecryptor - decrypt po ifie");

        AWSKMS kms = getKmsClient();
        logger.info("AwsKmsDecryptor - po getKmsClient");

        ByteBuffer encryptedBlob = ByteBuffer.wrap(Base64.getDecoder().decode(encryptedValue));
        DecryptResult result = kms.decrypt(new DecryptRequest().withCiphertextBlob(encryptedBlob));
        logger.info("AwsKmsDecryptor - po decrypcie");

        return new String(result.getPlaintext().array());
    }

    private boolean isEncrypted(String value) {

        return !value.startsWith(PLAINTEXT_SECRET_PREFIX);
    }

    AWSKMS getKmsClient() {

        return AWSKMSClientBuilder.standard().withRegion(region.getName()).build();
    }
}
