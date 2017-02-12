package uk.gov.dvsa.motr.subscriptionloader.processing.loader;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.PurgeQueueInProgressException;
import com.amazonaws.services.sqs.model.PurgeQueueRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class PurgingLoader implements Loader {

    private static final Logger logger = LoggerFactory.getLogger(PurgingLoader.class);
    private Loader wrappedLoader;

    private AmazonSQS awsSqs;

    private String queueUrl;

    public PurgingLoader(Loader wrappedLoader, AmazonSQS awsSqs, String queueUrl) {
        this.wrappedLoader = wrappedLoader;
        this.awsSqs = awsSqs;
        this.queueUrl = queueUrl;
    }

    @Override
    public void run(LocalDate today) throws Exception {

        final int postPurgeDelayMs = 60_000;

        long purgingStartedAt = System.currentTimeMillis();

        while (true) {

            try {
                logger.info("Requesting queue purge");

                awsSqs.purgeQueue(new PurgeQueueRequest().withQueueUrl(queueUrl));

                logger.info("Wait after purge operation to finish: {} ms", postPurgeDelayMs);

                Thread.sleep(postPurgeDelayMs);

                break;
            } catch (PurgeQueueInProgressException pqinpe) {
                // retry
                logger.info("Purging already in progress");
                Thread.sleep(10_000);
            }

        }

        logger.info("Purging completed in: {} ms", System.currentTimeMillis() - purgingStartedAt);

        wrappedLoader.run(today);
    }
}
