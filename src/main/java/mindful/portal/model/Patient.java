package mindful.portal.model;

import lombok.Data;

import java.util.List;

@Data
public class Patient {
    private PatientInfo patientInfo;
    private Address address;
    private Email email;
    private Phone phone;
}
