package uk.gov.dvsa.motr.datamock.model;

import java.time.LocalDate;

import javax.ws.rs.core.Response;

import static uk.gov.dvsa.motr.datamock.model.MockResponse.VehicleType.HGV;
import static uk.gov.dvsa.motr.datamock.model.MockResponse.VehicleType.MOT;
import static uk.gov.dvsa.motr.datamock.model.MockResponse.VehicleType.PSV;
import static uk.gov.dvsa.motr.datamock.model.MockResponse.VehicleType.Trailer;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

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
                    .dueDate(daysFromToday(60))
                    .testNumber("42343234");
        } else if (vrm.equals("SUB-CHANGE")) {
            response.make("MERCEDES-BENZ")
                    .model("C220 ELEGANCE ED125 CDI BLU-CY")
                    .primaryColour("Silver")
                    .vehicleType(MOT)
                    .manufactureYear("2006")
                    .dueDate("2016-11-26")
                    .testNumber("42343234");
        } else if (vrm.equals("HGV1MO")) {
            response.make("DAF")
                    .model("XF 105")
                    .vehicleType(HGV)
                    .manufactureYear("2008")
                    .dvlaId("42343234")
                    .dueDate(daysFromToday(30));
        } else if (vrm.equals("HGV2MO")) {
            response.make("DAF")
                    .model("XF 105")
                    .vehicleType(HGV)
                    .manufactureYear("2009")
                    .dvlaId("42343234")
                    .dueDate(daysFromToday(60));
        } else if (vrm.equals("PSV1MO")) {
            response.make("DAF")
                    .model("XF 105")
                    .vehicleType(PSV)
                    .manufactureYear("2009")
                    .testNumber("42343234")
                    .dueDate(daysFromToday(30));
        } else if (vrm.equals("PSV2MO")) {
            response.make("DAF")
                    .model("XF 105")
                    .vehicleType(PSV)
                    .manufactureYear("2008")
                    .testNumber("42343234")
                    .dueDate(daysFromToday(60));
        } else if (vrm.contains("YN13NTX")) {
            response.make("HARLEY-DAVIDSON CVO ROAD GLIDE FLTRXSE2 ANV 13")
                    .model("")
                    .primaryColour("Multi-colour")
                    .secondaryColour("Multi-colour")
                    .vehicleType(MOT)
                    .manufactureYear("2004")
                    .dueDate(daysFromToday(60))
                    .testNumber("42343234");
        } else if (vrm.contains("LOY-500")) {
            response.make("TOJEIRO BRISTOL 2.0L")
                    .model("1 DR MANUAL CONVERTIBLE SPORTS")
                    .primaryColour("Red")
                    .secondaryColour("Multi-colour")
                    .vehicleType(MOT)
                    .manufactureYear("1999")
                    .dueDate(daysFromToday(60))
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
                    .dueDate(daysFromToday(60))
                    .testNumber("12349876");
        } else if (vrm.contains("HGV-OLDEXPIRY")) {
            response.make("testMakeHGV")
                    .model("testModelHGV")
                    .primaryColour("testPrimaryColour")
                    .secondaryColour("testSecondaryColour")
                    .vehicleType(HGV)
                    .manufactureYear("1999")
                    .dueDate("2017-03-09")
                    .testNumber("12349876");
        } else if (vrm.contains("HGVMULTICOLOR")) {
            response.make("Mercedes-Benz")
                    .model("Antos")
                    .primaryColour("Multi-colour")
                    .secondaryColour("Multi-colour")
                    .vehicleType(HGV)
                    .manufactureYear("1999")
                    .dueDate("2025-03-09")
                    .dvlaId("344541")
                    .testNumber("124313");
        } else if (vrm.contains("HGV-ONECOLOR")) {
            response.make("Mercedes-Benz")
                    .model("Econic")
                    .primaryColour("White")
                    .vehicleType(HGV)
                    .manufactureYear("1999")
                    .dueDate("2022-01-09")
                    .dvlaId("344541")
                    .testNumber("124524");
        } else if (vrm.contains("HGV-NOTEST")) {
            response.make("Mercedes-Benz")
                    .model("Econic")
                    .primaryColour("White")
                    .vehicleType(HGV)
                    .manufactureYear("1999")
                    .dueDate("2026-01-09")
                    .dvlaId("46362");
        } else if (vrm.contains("PSV-NOTEST")) {
            response.make("testMakePSV")
                    .model("testModelPSV")
                    .primaryColour("testPrimaryColour")
                    .secondaryColour("testSecondaryColour")
                    .vehicleType(PSV)
                    .manufactureYear("1999")
                    .dueDate(daysFromToday(60))
                    .dvlaId("567623")
                    .testNumber("546431");
        } else if (vrm.contains("PSVMULTICOLOR")) {
            response.make("Mercedes-Benz")
                    .model("testPSVModel")
                    .primaryColour("Multi-colour")
                    .secondaryColour("Multi-colour")
                    .vehicleType(PSV)
                    .manufactureYear("1999")
                    .dueDate("2025-06-06")
                    .dvlaId("4537474")
                    .testNumber("43245");
        } else if (vrm.contains("PSV-ONECOLOR")) {
            response.make("Mercedes-Benz")
                    .model("PSVModel")
                    .primaryColour("White")
                    .vehicleType(PSV)
                    .manufactureYear("1999")
                    .dueDate("2022-02-01")
                    .dvlaId("213123")
                    .testNumber("6435231");
        } else if (vrm.contains("PSV-NOTEST")) {
            response.make("Mercedes-Benz")
                    .model("PSVTestModel")
                    .primaryColour("White")
                    .vehicleType(PSV)
                    .manufactureYear("1999")
                    .dueDate("2026-01-09")
                    .dvlaId("245436");
        } else if (vrm.contains("PSV-UNKNEXP")) {
            response.make("Mercedes-Benz")
                    .model("PSV-UNKN-1999")
                    .primaryColour("White")
                    .vehicleType(PSV)
                    .manufactureYear("1999");
        } else if (vrm.contains("HGV-UNKNEXP")) {
            response.make("Mercedes-Benz")
                    .model("HGV-UNKN-1999")
                    .primaryColour("White")
                    .vehicleType(HGV)
                    .manufactureYear("1999");
        } else if (vrm.contains("TEST-EXPIRED")) {
            response.make("Mercedes-Benz")
                    .model("PSVTestModel")
                    .primaryColour("White")
                    .vehicleType(PSV)
                    .manufactureYear("1999")
                    .dueDate("2016-01-09")
                    .dvlaId("245437")
                    .testNumber("2325326");
        } else if (vrm.contains("FIRST-EXP")) {
            response.make("Mercedes-Benz")
                    .model("MOTTestModel")
                    .primaryColour("White")
                    .vehicleType(MOT)
                    .manufactureYear("1999")
                    .dueDate("2015-01-09")
                    .dvlaId("245439");
        } else if (vrm.contains("HGV-FIRSTEXP")) {
            response.make("Mercedes-Benz")
                    .model("HGVTestModel")
                    .primaryColour("White")
                    .vehicleType(HGV)
                    .manufactureYear("1999")
                    .dueDate("2014-01-09")
                    .dvlaId("245440");
        } else if (vrm.contains("A112233")) {
            response.make("TrailerMake")
                    .model("TrailerTestModel")
                    .primaryColour("White")
                    .vehicleType(Trailer)
                    .manufactureYear("1999")
                    .dueDate("2014-01-09");
        } else if (vrm.contains("C123456")) {
            response.make("TrailerMake")
                    .model("TrailerTestModel")
                    .vehicleType(Trailer)
                    .manufactureYear("1999")
                    .dueDate("2026-01-09");
        } else if (vrm.contains("A111111")) {
            response.make("TrailerMake")
                    .model("TrailerTestModel")
                    .vehicleType(Trailer)
                    .primaryColour("Green")
                    .manufactureYear("2011")
                    .dueDate("2026-01-09");
        } else if (vrm.contains("A222222")) {
            response.make("TrailerMake")
                    .model("TrailerTestModel")
                    .vehicleType(Trailer)
                    .primaryColour("Yellow")
                    .manufactureYear("2011")
                    .testNumber("12345678")
                    .dueDate("2026-01-09");
        } else {
            response.make("testMake")
                    .model("testModel")
                    .primaryColour("testPrimaryColour")
                    .secondaryColour("testSecondaryColour")
                    .vehicleType(MOT)
                    .manufactureYear("1998")
                    .dueDate("2026-03-09")
                    .testNumber("2325325");
        }

        return Response.ok(response.vrm(vrm)).build();
    }

    private String daysFromToday(long days) {
        return LocalDate.now().plusDays(days).format(ISO_LOCAL_DATE);
    }
}

