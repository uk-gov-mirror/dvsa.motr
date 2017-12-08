package uk.gov.dvsa.motr.web.performance;

public class ColdStartMarker {

    private static boolean isSet = true;

    public static void unmark() {

        isSet = false;
    }

    public static boolean isSet() {
        
        return isSet;
    }
}
