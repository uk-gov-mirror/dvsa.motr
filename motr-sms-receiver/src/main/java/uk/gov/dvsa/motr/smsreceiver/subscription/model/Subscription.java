package uk.gov.dvsa.motr.smsreceiver.subscription.model;

import uk.gov.dvsa.motr.vehicledetails.VehicleType;

import java.time.LocalDate;
import java.util.Optional;

public class Subscription {

    private String unsubscribeId;

    private String vrm;

    private String contactDetail;

    private LocalDate motDueDate;

    private MotIdentification motIdentification;

    private VehicleType vehicleType;

    public String getUnsubscribeId() {
        return unsubscribeId;
    }

    public Subscription setUnsubscribeId(String id) {

        this.unsubscribeId = id;
        return this;
    }

    public String getVrm() {

        return vrm;
    }

    public Subscription setVrm(String vrm) {

        this.vrm = vrm;
        return this;
    }

    public String getContactDetail() {

        return contactDetail;
    }

    public Subscription setContactDetail(String contactDetail) {

        this.contactDetail = contactDetail;
        return this;
    }

    public LocalDate getMotDueDate() {

        return motDueDate;
    }

    public Subscription setMotDueDate(LocalDate motDueDate) {

        this.motDueDate = motDueDate;
        return this;
    }

    public MotIdentification getMotIdentification() {

        return motIdentification;
    }

    public Subscription setMotIdentification(MotIdentification motIdentification) {

        this.motIdentification = motIdentification;
        return this;
    }

    public VehicleType getVehicleType() {
        return Optional.ofNullable(vehicleType)
                .orElse(VehicleType.getDefault());
    }

    public Subscription setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
        return this;
    }
}

