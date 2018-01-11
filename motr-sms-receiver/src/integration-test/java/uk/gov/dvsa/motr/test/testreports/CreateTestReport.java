package uk.gov.dvsa.motr.test.testreports;

import org.junit.Test;

import uk.gov.dvsa.motr.testreports.TestReports;

public class CreateTestReport {

    public static void main(String...args)  {

        TestReports.createTestReport(args[0],"uk.gov.dvsa.motr.test", Test.class);
    }

}
