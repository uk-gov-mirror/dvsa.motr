package uk.gov.dvsa.motr.web.test.aws;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

import java.util.UUID;

public class TestLambdaContext implements Context {

    @Override
    public String getAwsRequestId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String getLogGroupName() {
        return "testLogGroupName";
    }

    @Override
    public String getLogStreamName() {
        return "testLogStreamName";
    }

    @Override
    public String getFunctionName() {
        return "testFunctionName";
    }

    @Override
    public String getFunctionVersion() {
        return "testFunctionVersion";
    }

    @Override
    public String getInvokedFunctionArn() {
        return "testInvokedFunctionArn";
    }

    @Override
    public CognitoIdentity getIdentity() {
        return null;
    }

    @Override
    public ClientContext getClientContext() {
        return null;
    }

    @Override
    public int getRemainingTimeInMillis() {
        return 0;
    }

    @Override
    public int getMemoryLimitInMB() {
        return 0;
    }

    @Override
    public LambdaLogger getLogger() {
        return null;
    }
}
