package uk.gov.dvsa.motr.datamock.model;

import javax.ws.rs.core.Response;

public class ByDvlaIdResolver {

    public Response resolve(String dvlaId) {

        if (dvlaId.startsWith("ERROR")) {
            return Response.status(Integer.valueOf(dvlaId.substring(5))).build();
        }

        MockResponse response = new MockResponse();

        if (dvlaId.contains("12345")) {
            response = new MockResponse()
                    .make("MERCEDES-BENZ")
                    .model("C220 ELEGANCE ED125 CDI BLU-CY")
                    .primaryColour("Silver")
                    .vrm("WDD2040022A65")
                    .manufactureYear("2006")
                    .dueDate("2016-11-26")
                    .testNumber("894329854");
        } else if (dvlaId.contains("412321")) {
            response = new MockResponse()
                    .make("HYUNDAI")
                    .model("I30 CRDI")
                    .primaryColour("Silver")
                    .vrm("SUP4R")
                    .manufactureYear("2006")
                    .dueDate("2007-11-26");
        } else {
            response.make("testMakeX")
                    .model("testModel")
                    .primaryColour("testPrimaryColour")
                    .secondaryColour("testSecondaryColour")
                    .vrm("LOCA111")
                    .manufactureYear("1998")
                    .dueDate("2000-03-09");
        }

        return Response.ok(response.dvlaId(dvlaId)).build();
    }
}