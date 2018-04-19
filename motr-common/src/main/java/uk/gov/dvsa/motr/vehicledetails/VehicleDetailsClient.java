package uk.gov.dvsa.motr.vehicledetails;

import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * HTTP client to retrieve vehicle and test data
 */
public class VehicleDetailsClient {

    private static final Logger logger = LoggerFactory.getLogger(VehicleDetailsClient.class);

    private static final String MOT_TESTS_PATH_PARAM = "number";
    private static final String DVLA_ID_PATH_PARAM = "dvlaId";
    private static final String REG_NUMBER_QUERY_PARAM = "registration";
    private static final String API_KEY_HEADER = "x-api-key";

    private Client client;
    private String uriByMotTestNumber;
    private String uriByDvlaId;
    private String uriByRegNumber;
    private String apiKey;
    private boolean hgvPsvFeatureToggle;

    public VehicleDetailsClient(ClientConfig clientConfig, String apiKey) {

        this.apiKey = apiKey;
        this.client = ClientBuilder.newClient(clientConfig);
    }

    /**
     * Method to fetch vehicle information
     * @param vrm vehicle registration mark
     * @return vehicle data {@link VehicleDetailsClient}
     * @throws VehicleDetailsClientException thrown when unexpected response (5XX, garbled response, timeout, etc)
     */
    public Optional<VehicleDetails> fetchByVrm(String vrm) throws VehicleDetailsClientException {

        if (hgvPsvFeatureToggle) {
            return fetchWithQueryParam(vrm, uriByRegNumber, REG_NUMBER_QUERY_PARAM);
        }

        return fetch(vrm, uriByRegNumber, REG_NUMBER_QUERY_PARAM);
    }

    /**
     * Method to fetchByMotTestNumber vehicle information with custom api key
     * @param motTestNumber vehicle registration mark
     * @return vehicle data {@link VehicleDetailsClient}
     * @throws VehicleDetailsClientException thrown when unexpected response (5XX, garbled response, timeout, etc)
     */
    public Optional<VehicleDetails> fetchByMotTestNumber(String motTestNumber) throws VehicleDetailsClientException {

        return fetch(motTestNumber, uriByMotTestNumber, MOT_TESTS_PATH_PARAM);
    }

    /**
     * Method to fetchByDvlaId vehicle information with custom api key
     * @param dvlaId vehicle registration mark
     * @return vehicle data {@link VehicleDetailsClient}
     * @throws VehicleDetailsClientException thrown when unexpected response (5XX, garbled response, timeout, etc)
     */
    public Optional<VehicleDetails> fetchByDvlaId(String dvlaId) throws VehicleDetailsClientException {

        return fetch(dvlaId, uriByDvlaId, DVLA_ID_PATH_PARAM);
    }

    private Optional<VehicleDetails> fetch(String val, String uri, String pathParam) throws VehicleDetailsClientException {

        if (uri == null) {
            throw new VehicleDetailsClientException("URI not configured for API method in MOTR");
        }

        Response response;
        try {
            WebTarget target = this.client.target(uri)
                    .resolveTemplate(pathParam, val);

            response = this.client.target(target.getUri())
                    .request()
                    .header(API_KEY_HEADER, apiKey)
                    .get();

        } catch (ProcessingException processingException) {

            throw new VehicleDetailsClientException(processingException);
        }

        return processResponse(response);
    }

    private Optional<VehicleDetails> fetchWithQueryParam(String val, String uri, String queryParam) throws VehicleDetailsClientException {

        if (uri == null) {
            throw new VehicleDetailsClientException("URI not configured for API method in MOTR");
        }

        Response response;
        try {
            WebTarget target = this.client.target(uri)
                    .queryParam(queryParam, val);

            response = this.client.target(target.getUri())
                    .request()
                    .header(API_KEY_HEADER, apiKey)
                    .get();

        } catch (ProcessingException processingException) {
            throw new VehicleDetailsClientException(processingException);
        }

        return processResponse(response);
    }

    private Optional<VehicleDetails> processResponse(Response response) throws VehicleDetailsClientException {

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

    public VehicleDetailsClient withByMotTestNumberUri(String uriByMotTestNumber) {

        this.uriByMotTestNumber = uriByMotTestNumber;
        return this;
    }

    public VehicleDetailsClient withByDvlaIdUri(String uriByDvlaId) {

        this.uriByDvlaId = uriByDvlaId;
        return this;
    }

    public VehicleDetailsClient withByRegNumberUri(String uriByRegNumber) {

        this.uriByRegNumber = uriByRegNumber;
        return this;
    }

    public VehicleDetailsClient withHgvPsvFeatureToggle(boolean hgvPsvFeatureToggle) {
        this.hgvPsvFeatureToggle = hgvPsvFeatureToggle;
        return this;
    }
}
