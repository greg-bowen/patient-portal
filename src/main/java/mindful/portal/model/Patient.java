package mindful.portal.model;

import lombok.Data;

@Data
public class Patient {
    private PatientInfo patientInfo;
    private Address address;
    private Email email;
    private Phone phone;
}
