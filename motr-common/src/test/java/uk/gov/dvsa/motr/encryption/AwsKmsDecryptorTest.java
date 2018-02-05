package uk.gov.dvsa.motr.encryption;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.model.DecryptResult;

import org.junit.Test;

import java.util.Base64;

import static com.amazonaws.regions.Region.getRegion;
import static com.amazonaws.regions.Regions.EU_WEST_1;

import static org.junit.Assert.assertEquals;

import static java.nio.ByteBuffer.wrap;

public class AwsKmsDecryptorTest {

    @Test
    public void decryptorIsCorrectlyProxyingDataToKms() {

        byte[] plainTextAsBytes = "Some decrypted String".getBytes();
        byte[] cipherTextAsBytes = "ThisIsBase64EncodedCipherBlob".getBytes();
        DecryptResult expectedDecryptResult = new DecryptResult().withPlaintext(wrap(plainTextAsBytes));
        AwsKmsStub kmsStub = new AwsKmsStub().setExpectedDecryptResult(expectedDecryptResult);

        Decryptor decryptor = new AwsKmsDecryptor(Region.getRegion(Regions.EU_WEST_1)) {
            @Override
            AWSKMS getKmsClient() {
                return kmsStub;
            }
        };

        assertEquals("Decryption result is different than expected",
                new String(plainTextAsBytes), decryptor.decrypt(Base64.getEncoder().encodeToString(cipherTextAsBytes)));

        assertEquals("Encrypted string was not passed correctly to KMS", wrap(cipherTextAsBytes),
                kmsStub.getDecryptRequest().getCiphertextBlob());
    }

    @Test
    public void decryptorIsCorrectlyIgnoringKmsWhenSecretSpecifiesIsPlaintext() {

        byte[] plainTextAsBytes = "SomeString".getBytes();
        byte[] cipherTextAsBytes = "PLAINTEXT_SomeString".getBytes();
        Region expectedRegion = getRegion(EU_WEST_1);

        Decryptor decryptor = new AwsKmsDecryptor(expectedRegion);

        assertEquals("Decryption result is different than expected",
                new String(plainTextAsBytes), decryptor.decrypt(new String(cipherTextAsBytes)));
    }
}
