package uk.gov.dvsa.motr.datamock.model;

import javax.ws.rs.core.Response;

public class ByTestNumberResolver {

    public Response resolve(String testNumber) {

        if (testNumber.startsWith("ERROR")) {
            return Response.status(Integer.valueOf(testNumber.substring(5))).build();
        }

        MockResponse response = new MockResponse();

        if (testNumber.contains("12345")) {
            response = new MockResponse()
                    .make("MERCEDES-BENZ")
                    .model("C220 ELEGANCE ED125 CDI BLU-CY")
                    .primaryColour("Silver")
                    .vrm("WDD2040022A65")
                    .manufactureYear("2006")
                    .dueDate("2016-11-26")
                    .testNumber("12345");
        } else {
            response.make("testMake")
                    .model("testModel")
                    .primaryColour("testPrimaryColour")
                    .secondaryColour("testSecondaryColour")
                    .vrm("XXXYYY")
                    .manufactureYear("1998")
                    .testNumber("2321321")
                    .dueDate("2026-03-09");
        }

        return Response.ok(response).build();
    }
}