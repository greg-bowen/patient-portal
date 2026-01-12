package mindful.portal.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(schema = "core_bio" , name = "emails")
public class Email {
    @Id
    private long id;

    private long patientId;
    private String emailAddress;
    private LocalDateTime expirationDate;
}
