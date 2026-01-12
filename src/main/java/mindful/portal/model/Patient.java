package mindful.portal.model;

import lombok.Data;

import java.util.List;

@Data
public class Patient {
    private PatientInfo patientInfo;
    private List<Address> address;
    private Email email;
    private List<Phone> phone;
}
