package uk.gov.dvsa.motr.subscriptionloader.processing.loader;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.PurgeQueueInProgressException;
import com.amazonaws.services.sqs.model.PurgeQueueRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class PurgingLoader implements Loader {

    private static final Logger logger = LoggerFactory.getLogger(PurgingLoader.class.getSimpleName());

    private Loader wrappedLoader;
    private AmazonSQS awsSqs;
    private String queueUrl;
    private int postPurgeDelayMs;
    private int purgeInProgressDelayMs;

    public PurgingLoader(Loader wrappedLoader, AmazonSQS awsSqs, String queueUrl, int postPurgeDelayMs, int purgeInProgressDelayMs) {

        this.wrappedLoader = wrappedLoader;
        this.awsSqs = awsSqs;
        this.queueUrl = queueUrl;
        this.postPurgeDelayMs = postPurgeDelayMs;
        this.purgeInProgressDelayMs = purgeInProgressDelayMs;
    }

    @Override
    public LoadReport run(LocalDate today, Context context) throws Exception {

        //        long purgingStartedAt = System.currentTimeMillis();
        //
        //        while (true) {
        //            try {
        //                logger.info("Requesting queue purge");
        //
        //                awsSqs.purgeQueue(new PurgeQueueRequest().withQueueUrl(queueUrl));
        //
        //                logger.info("Wait for purge operation to finish: {} ms", this.postPurgeDelayMs);
        //
        //                Thread.sleep(this.postPurgeDelayMs);
        //
        //                break;
        //            } catch (PurgeQueueInProgressException pqinpe) {
        //                // retry
        //                logger.info("Purging already in progress");
        //                Thread.sleep(this.purgeInProgressDelayMs);
        //            }
        //        }
        //
        //        logger.info("Purging completed in: {} ms", System.currentTimeMillis() - purgingStartedAt);

        return wrappedLoader.run(today, context);
    }
}
