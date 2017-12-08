package uk.gov.dvsa.motr.remote.vehicledetails;

import static java.lang.String.format;

public class VehicleDetailsEndpointResponseException extends VehicleDetailsClientException {

    private int statusCode;

    private String body;

    public VehicleDetailsEndpointResponseException(int statusCode, String body) {
        super(format("Status code: %d, response body: %s", statusCode, body));

        this.statusCode = statusCode;
        this.body = body;
    }

    public int getStatusCode() {

        return statusCode;
    }

    public String getBody() {

        return body;
    }
}
