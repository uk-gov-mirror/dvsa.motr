package uk.gov.dvsa.motr.datamock.model;

import javax.ws.rs.core.Response;

import static uk.gov.dvsa.motr.datamock.model.MockResponse.VehicleType.HGV;
import static uk.gov.dvsa.motr.datamock.model.MockResponse.VehicleType.MOT;
import static uk.gov.dvsa.motr.datamock.model.MockResponse.VehicleType.PSV;

public class ByVrmResolver {

    public Response resolve(String vrm) {

        if (vrm.startsWith("ERROR")) {
            return Response.status(Integer.valueOf(vrm.substring(5))).build();
        }

        MockResponse response = new MockResponse();

        if (vrm.contains("WDD2040022A65")) {
            response.make("MERCEDES-BENZ")
                    .model("C220 ELEGANCE ED125 CDI BLU-CY")
                    .primaryColour("Silver")
                    .vehicleType(MOT)
                    .manufactureYear("2006")
                    .dueDate("2020-11-26")
                    .testNumber("42343234");
        } else if (vrm.contains("YN13NTX")) {
            response.make("HARLEY-DAVIDSON CVO ROAD GLIDE FLTRXSE2 ANV 13")
                    .model("")
                    .primaryColour("Multi-colour")
                    .secondaryColour("Multi-colour")
                    .vehicleType(MOT)
                    .manufactureYear("2004")
                    .dueDate("2020-12-01")
                    .testNumber("42343234");
        } else if (vrm.contains("LOY-500")) {
            response.make("TOJEIRO BRISTOL 2.0L")
                    .model("1 DR MANUAL CONVERTIBLE SPORTS")
                    .primaryColour("Red")
                    .secondaryColour("Multi-colour")
                    .vehicleType(MOT)
                    .manufactureYear("1999")
                    .dueDate("2020-08-14")
                    .testNumber("3243432");
        } else if (vrm.contains("OLD-EXPIRY-")) {
            response.make("testMake")
                    .model("testModel")
                    .primaryColour("testPrimaryColour")
                    .secondaryColour("testSecondaryColour")
                    .vehicleType(MOT)
                    .manufactureYear("1998")
                    .dueDate("2017-03-09")
                    .testNumber("532523");
        } else if (vrm.contains("DVLA-ID-")) {
            response.make("testDVLAMake")
                    .model("testDVLAModel")
                    .primaryColour("testDVLAPrimaryColour")
                    .secondaryColour("testDVLASecondaryColour")
                    .vehicleType(MOT)
                    .manufactureYear("1999")
                    .dueDate("2020-03-09")
                    .testNumber("12349876");
        } else if (vrm.contains("HGV-OLDEXPIRY")) {
            response.make("testMakeHGV")
                    .model("testModelHGV")
                    .primaryColour("testPrimaryColour")
                    .secondaryColour("testSecondaryColour")
                    .vehicleType(HGV)
                    .vin("HGV23528765")
                    .manufactureYear("1999")
                    .dueDate("2020-03-09")
                    .testNumber("12349876");
        } else if (vrm.contains("HGVMULTICOLOR")) {
            response.make("Mercedes-Benz")
                    .model("Antos")
                    .primaryColour("Multi-colour")
                    .secondaryColour("Multi-colour")
                    .vehicleType(HGV)
                    .vin("HGV73525231")
                    .manufactureYear("1999")
                    .dueDate("2025-03-09")
                    .dvlaId("344541")
                    .testNumber("124313");
        } else if (vrm.contains("HGV-ONECOLOR")) {
            response.make("Mercedes-Benz")
                    .model("Econic")
                    .primaryColour("White")
                    .vehicleType(HGV)
                    .vin("HGV12574352")
                    .manufactureYear("1999")
                    .dueDate("2022-01-09")
                    .dvlaId("344541")
                    .testNumber("124524");
        } else if (vrm.contains("HGV-NOTEST")) {
            response.make("Mercedes-Benz")
                    .model("Econic")
                    .primaryColour("White")
                    .vehicleType(HGV)
                    .vin("HGV15423")
                    .manufactureYear("1999")
                    .dueDate("2026-01-09")
                    .dvlaId("46362");
        } else if (vrm.contains("PSV-NOTEST")) {
            response.make("testMakePSV")
                    .model("testModelPSV")
                    .primaryColour("testPrimaryColour")
                    .secondaryColour("testSecondaryColour")
                    .vehicleType(PSV)
                    .vin("PSV543435")
                    .manufactureYear("1999")
                    .dueDate("2017-01-04")
                    .dvlaId("567623")
                    .testNumber("546431");
        } else if (vrm.contains("PSVMULTICOLOR")) {
            response.make("Mercedes-Benz")
                    .model("testPSVModel")
                    .primaryColour("Multi-colour")
                    .secondaryColour("Multi-colour")
                    .vehicleType(PSV)
                    .vin("PSV7352324231")
                    .manufactureYear("1999")
                    .dueDate("2025-06-06")
                    .dvlaId("4537474")
                    .testNumber("43245");
        } else if (vrm.contains("PSV-ONECOLOR")) {
            response.make("Mercedes-Benz")
                    .model("PSVModel")
                    .primaryColour("White")
                    .vehicleType(PSV)
                    .vin("PSV7352324231")
                    .manufactureYear("1999")
                    .dueDate("2022-02-01")
                    .dvlaId("213123")
                    .testNumber("6435231");
        } else if (vrm.contains("PSV-NOTEST")) {
            response.make("Mercedes-Benz")
                    .model("PSVTestModel")
                    .primaryColour("White")
                    .vehicleType(PSV)
                    .vin("PSV154142323")
                    .manufactureYear("1999")
                    .dueDate("2026-01-09")
                    .dvlaId("245436");
        } else {
            response.make("testMake")
                    .model("testModel")
                    .primaryColour("testPrimaryColour")
                    .secondaryColour("testSecondaryColour")
                    .vehicleType(PSV)
                    .manufactureYear("1998")
                    .dueDate("2026-03-09")
                    .testNumber("2325325");
        }

        return Response.ok(response.vrm(vrm)).build();
    }
}