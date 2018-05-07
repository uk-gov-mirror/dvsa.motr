package uk.gov.dvsa.motr.datamock.handler;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(DataProviderRunner.class)
public class EventHandlerTest {

    @Test
    public void searchByVrmReturnsMockDataAsJson() {

        AwsProxyResponse response = new EventHandler().handle(requestWithParam("vrm", "WDD2040022A65"), null);

        assertEquals(200, response.getStatusCode());
        assertEquals("MERCEDES-BENZ", asJson(response.getBody()).get("make").asText());
    }

    @DataProvider
    public static Object[][] errorDataProvider() {
        return new Object[][]{{404}, {403}, {503}};
    }

    @UseDataProvider("errorDataProvider")
    @Test
    public void searchByVrmReturnsErrorAsRequired(int statusCode) {

        AwsProxyResponse response = new EventHandler().handle(requestWithParam("vrm", "ERROR" + statusCode), null);
        assertEquals(statusCode, response.getStatusCode());
    }

    @Test
    public void searchByVrmReturnsMockDataWithNullsNotMapped() {

        AwsProxyResponse response = new EventHandler().handle(requestWithParam("vrm", "HGV-ONECOLOR"), null);

        assertFalse(asJson(response.getBody()).has("secondaryColour"));
    }

    @Test
    public void legacySearchByTestNumberReturnsMockDataAsJson() {

        AwsProxyResponse response = new EventHandler().handle(legacyTestNumberRequest("12345"), null);

        assertEquals(200, response.getStatusCode());
        assertEquals("WDD2040022A65", asJson(response.getBody()).get("registration").asText());
    }

    @Test
    public void legacySearchByDvlaIdReturnsMockDataAsJson() {

        AwsProxyResponse response = new EventHandler().handle(legacyDvlaIdRequest("412321"), null);

        assertEquals(200, response.getStatusCode());
        assertEquals("SUP4R", asJson(response.getBody()).get("registration").asText());
    }

    @Test
    public void legacySearchByVrmReturnsMockDataAsJson() {

        AwsProxyResponse response = new EventHandler().handle(legacyVrmRequest("WDD2040022A65"), null);

        assertEquals(200, response.getStatusCode());
        assertEquals("MERCEDES-BENZ", asJson(response.getBody()).get("make").asText());
    }

    private JsonNode asJson(String string) {

        try {
            return new ObjectMapper().readTree(string);
        } catch (Exception e) {
            return null;
        }
    }

    private AwsProxyRequest requestWithParam(String queryParam, String value) {

        AwsProxyRequest request = new AwsProxyRequest();
        request.setPath("/mot-test-reminder-mock/motr/v2/search");
        request.setHttpMethod("GET");
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(queryParam, value);
        request.setQueryStringParameters(queryParams);
        return request;
    }

    private AwsProxyRequest legacyDvlaIdRequest(String value) {

        AwsProxyRequest request = new AwsProxyRequest();
        request.setPath("/mot-test-reminder-mock/mot-tests-by-dvla-id/" + value);
        request.setHttpMethod("GET");
        return request;
    }

    private AwsProxyRequest legacyTestNumberRequest(String value) {

        AwsProxyRequest request = new AwsProxyRequest();
        request.setPath("/mot-test-reminder-mock/mot-tests/" + value);
        request.setHttpMethod("GET");
        return request;
    }

    private AwsProxyRequest legacyVrmRequest(String value) {

        AwsProxyRequest request = new AwsProxyRequest();
        request.setPath("/mot-test-reminder-mock/vehicles/" + value);
        request.setHttpMethod("GET");
        return request;
    }
}
