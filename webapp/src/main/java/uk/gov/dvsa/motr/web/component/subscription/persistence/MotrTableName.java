package uk.gov.dvsa.motr.web.component.subscription.persistence;

public final class MotrTableName {

    public static String tableName(String envId, String coreName) {
        return String.format("motr-%s-%s", envId, coreName);
    }
}
