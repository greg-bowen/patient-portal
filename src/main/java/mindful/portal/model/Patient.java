package mindful.portal.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Patient {
    private int patientId;
    private String preferredName;
    private String firstName;
    private String middleName;
    private String lastName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dob;
    private String gender;
    private List<Address> address;
    private String email;
    private List<Phone> phone;
}
