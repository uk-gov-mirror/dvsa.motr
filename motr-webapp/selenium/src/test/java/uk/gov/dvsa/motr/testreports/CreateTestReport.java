package uk.gov.dvsa.motr.testreports;

import org.testng.annotations.Test;

public class CreateTestReport {

    public static void main(String...args)  {

        TestReports.createTestReport(args[0],"uk.gov.dvsa.motr.journey", Test.class);
    }
}
