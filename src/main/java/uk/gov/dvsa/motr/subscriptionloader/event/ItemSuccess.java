package uk.gov.dvsa.motr.subscriptionloader.event;

import uk.gov.dvsa.motr.eventlog.Event;

import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ISO_DATE;

public class ItemSuccess extends Event {

    @Override
    public String getCode() {

        return "ITEM-SUCCESS";
    }

    public ItemSuccess setVrm(String vrm) {

        params.put("vrm", vrm);
        return this;
    }

    public ItemSuccess setEmail(String email) {

        params.put("email", email);
        return this;
    }

    public ItemSuccess setId(String id) {

        params.put("id", id);
        return this;
    }

    public ItemSuccess setDueDate(LocalDate dueDate) {

        params.put("due-date", dueDate.format(ISO_DATE));
        return this;
    }
}
