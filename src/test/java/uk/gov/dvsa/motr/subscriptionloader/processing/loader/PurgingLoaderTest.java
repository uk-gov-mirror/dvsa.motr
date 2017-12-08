package uk.gov.dvsa.motr.subscriptionloader.processing.loader;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.PurgeQueueRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PurgingLoaderTest {

    private Loader wrappedLoader = mock(Loader.class);
    private AmazonSQS awsSqs = mock(AmazonSQS.class);
    private String queueUrl;
    private int postPurgeDelayMs = 0;
    private int purgeInProgressDelayMs = 0;
    private PurgingLoader purgingLoader;
    private Context context = mock(Context.class);

    @Before
    public void setup() {
        this.purgingLoader = new PurgingLoader(
                this.wrappedLoader, this.awsSqs, this.queueUrl,
                this.postPurgeDelayMs, this.purgeInProgressDelayMs);
    }

    @Test
    public void whenPurgeIsRun_thenCallMadeToPurgeQueue() throws Exception {

        when(this.awsSqs.purgeQueue(any())).thenReturn(null);

        this.purgingLoader.run(LocalDate.now(), context);

        verify(this.awsSqs, times(1)).purgeQueue(any(PurgeQueueRequest.class));
    }

    @Test
    public void whenPurgeCalled_thenLoaderIsCalled() throws Exception {

        when(this.awsSqs.purgeQueue(any())).thenReturn(null);

        LoadReport report = new LoadReport();
        when(this.wrappedLoader.run(any(), any())).thenReturn(report);

        LocalDate now = LocalDate.now();
        LoadReport returnedReport = this.purgingLoader.run(now, context);

        verify(this.wrappedLoader, times(1)).run(eq(now), any());
        Assert.assertSame(report, returnedReport);
    }

}
