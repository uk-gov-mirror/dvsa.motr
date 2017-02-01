package uk.gov.dvsa.motr.web.component.subscription.persistence;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import static uk.gov.dvsa.motr.web.component.subscription.persistence.MotrTableName.tableName;

public class MotrTableNameTest {

    @Test
    public void tableNameBuiltCorrectly() {

        assertEquals("motr-myEnv-core_table_name", tableName("myEnv", "core_table_name"));
    }
}
