package mindful.portal.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Generated;

import java.time.LocalDateTime;

@Data
@Entity
@Table(schema = "core_bio", name = "phone")
public class Phone {
    @Id
    private long id;

    private long patientId;
    private String phoneNumber;
    private Boolean sms;
    private Character type;
    private LocalDateTime expirationDate;
}
