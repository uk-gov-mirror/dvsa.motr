package uk.gov.dvsa.motr.remote.vehicledetails;

import org.glassfish.jersey.client.ClientConfig;

import java.util.Optional;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

/**
 * HTTP client to retrieve vehicle and test data
 */
public class VehicleDetailsClient {

    private static final String REG_NUMBER_QUERY_PARAM = "vrm";
    private static final String API_KEY_HEADER = "x-api-key";

    private Client client;

    private String uri;

    private String apiKey;

    public VehicleDetailsClient(ClientConfig clientConfig, String endpointUri, String apiKey) {

        this.uri = endpointUri;
        this.apiKey = apiKey;
        this.client = ClientBuilder.newClient(clientConfig);
    }

    /**
     * Method to fetch vehicle information
     * @param vrm vehicle registration mark
     * @return vehicle data {@link VehicleDetailsClient}
     * @throws VehicleDetailsClientException thrown when unexpected response (5XX, garbled response, timeout, etc)
     */
    public Optional<VehicleDetails> fetch(String vrm) throws VehicleDetailsClientException {

        return fetch(vrm, this.apiKey);
    }

    /**
     * Method to fetch vehicle information with custom api key
     * @param vrm vehicle registration mark
     * @param apiKey custom api key
     * @return vehicle data {@link VehicleDetailsClient}
     * @throws VehicleDetailsClientException thrown when unexpected response (5XX, garbled response, timeout, etc)
     */
    public Optional<VehicleDetails> fetch(String vrm, String apiKey) throws VehicleDetailsClientException {

        Response response;

        try {
            response = this.client.target(uri)
                    .queryParam(REG_NUMBER_QUERY_PARAM, vrm)
                    .request()
                    .header(API_KEY_HEADER, apiKey)
                    .get();

        } catch (ProcessingException processingException) {

            throw new VehicleDetailsClientException(processingException);
        }

        return processResponse(response);
    }

    private static Optional<VehicleDetails> processResponse(Response response) throws VehicleDetailsClientException {


        int responseStatus = response.getStatus();

        if (responseStatus == Response.Status.OK.getStatusCode()) {

            try {

                return Optional.of(response.readEntity(VehicleDetails.class));

            } catch (Exception readingException) {
                throw new VehicleDetailsClientException(readingException);
            }

        } else if (responseStatus == Response.Status.NOT_FOUND.getStatusCode()) {

            return Optional.empty();
        } else {

            String content = "";
            try {
                if (response.hasEntity()) {
                    content = response.readEntity(String.class);
                }
            } catch (Exception readingException) {
                throw new VehicleDetailsClientException(readingException);
            }

            throw new VehicleDetailsEndpointResponseException(responseStatus, content);
        }
    }
}
