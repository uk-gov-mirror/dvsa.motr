package uk.gov.dvsa.motr.remote.vehicledetails;

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

    private static final String API_KEY_HEADER = "x-api-key";

    private Client client;

    private String uriByMotTestNumber;
    private String uriByDvlaId;

    private String apiKey;

    public VehicleDetailsClient(
            ClientConfig clientConfig,
            String endpointUriByMotTestNumber,
            String apiKey,
            String endpointUriByDvlaId) {

        this.uriByMotTestNumber = endpointUriByMotTestNumber;
        this.apiKey = apiKey;
        this.client = ClientBuilder.newClient(clientConfig);
        this.uriByDvlaId = endpointUriByDvlaId;
    }

    /**
     * Method to fetchByMotTestNumber vehicle information with custom api key
     * @param motTestNumber vehicle registration mark
     * @return vehicle data {@link VehicleDetailsClient}
     * @throws VehicleDetailsClientException thrown when unexpected response (5XX, garbled response, timeout, etc)
     */
    public Optional<VehicleDetails> fetchByMotTestNumber(String motTestNumber) throws VehicleDetailsClientException {

        logger.trace("entered fetch by mot test number method in VehicleDetailsClient");
        Response response;
        try {

            WebTarget target = this.client.target(this.uriByMotTestNumber)
                    .resolveTemplate(MOT_TESTS_PATH_PARAM, motTestNumber);

            response = this.client.target(target.getUri())
                    .request()
                    .header(API_KEY_HEADER, apiKey)
                    .get();

        } catch (ProcessingException processingException) {

            throw new VehicleDetailsClientException(processingException);
        }

        return processResponse(response);
    }

    /**
     * Method to fetchByDvlaId vehicle information with custom api key
     * @param dvlaId vehicle registration mark
     * @return vehicle data {@link VehicleDetailsClient}
     * @throws VehicleDetailsClientException thrown when unexpected response (5XX, garbled response, timeout, etc)
     */
    public Optional<VehicleDetails> fetchByDvlaId(String dvlaId) throws VehicleDetailsClientException {

        logger.trace("entered fetch by dvlaId method in VehicleDetailsClient");
        Response response;
        try {

            WebTarget target = this.client.target(this.uriByDvlaId)
                    .resolveTemplate(DVLA_ID_PATH_PARAM, dvlaId);

            response = this.client.target(target.getUri())
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
