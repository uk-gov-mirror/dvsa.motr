package uk.gov.dvsa.motr.web.handler;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import uk.gov.dvsa.motr.web.performance.warmup.PingAwareAwsProxyRequest;
import uk.gov.dvsa.motr.web.test.TestMotrWebHandler;
import uk.gov.dvsa.motr.web.test.aws.TestLambdaContext;
import uk.gov.dvsa.motr.web.test.environment.TestEnvironmentVariables;

import static org.junit.Assert.assertNull;

public class PingAwareMotrWebHandlerTest {

    @Rule
    public final EnvironmentVariables environmentVariables = new TestEnvironmentVariables();

    private final MotrWebHandler handler = new TestMotrWebHandler();

    @Test
    public void handlesReturnsWithNullWhenPingRequested() throws Exception {

        PingAwareAwsProxyRequest req = new PingAwareAwsProxyRequest();
        req.setPing(true);

        assertNull(handle(req));
    }

    private AwsProxyResponse handle(PingAwareAwsProxyRequest req) {

        return handler.handleRequest(req, new TestLambdaContext());
    }
}
