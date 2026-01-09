package mindful.portal.model;


import lombok.Data;

@Data
public class Request {
    private int patientId;
    private Phone phone;
    private Address address;
    private Email email;
}
