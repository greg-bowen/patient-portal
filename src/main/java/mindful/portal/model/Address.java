package mindful.portal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(schema = "core_bio", name = "addresses")
public class Address {
    @Id
    private long id;

    private long patientId;
    @Column(name = "address_line_1")
    private String addressLine1;
    @Column(name = "address_line_2")
    private String addressLine2;
    private String city;
    private String state;
    private String zip;
    private Character type;
    private LocalDateTime effective_date;
    private LocalDateTime expiration_date;
}
