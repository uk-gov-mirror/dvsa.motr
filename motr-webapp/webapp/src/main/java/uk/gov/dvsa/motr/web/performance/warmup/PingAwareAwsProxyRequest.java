package uk.gov.dvsa.motr.web.performance.warmup;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;

/**
 * Request envelope that contains information about the calling context - whether the Lambda function
 * has been invoked in the PING/WARMUP mode (e.g. by Cloudwatch Events) or through API Gateway
 */
public class PingAwareAwsProxyRequest extends AwsProxyRequest {

    private boolean isPing = false;

    public boolean isPing() {
        return isPing;
    }

    public void setPing(boolean isPing) {
        this.isPing = isPing;
    }
}