package uk.gov.dvsa.motr.test.integration.lambda;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;

import static uk.gov.dvsa.motr.test.environmant.variables.TestEnvironmentVariables.loaderFunctionName;
import static uk.gov.dvsa.motr.test.environmant.variables.TestEnvironmentVariables.region;

public class LoaderHelper {

    private AWSLambda lambdaClient;

    public LoaderHelper() {

        lambdaClient = AWSLambdaClientBuilder.standard().withRegion(region())
                .withCredentials(new DefaultAWSCredentialsProviderChain()).build();
    }

    public InvokeResult invokeLoader(String payload) {

        InvokeRequest request = new InvokeRequest();
        request.withFunctionName(loaderFunctionName()).withPayload(payload);
        InvokeResult invokeResult = lambdaClient.invoke(request);
        return invokeResult;
    }

}
