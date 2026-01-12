package mindful.portal.model.legacy;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_roles", schema = "public")
public class UserRole {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "roles_id_seq"
    )
    @SequenceGenerator(
            name = "roles_id_seq",
            sequenceName = "roles_id_seq",
            allocationSize = 1
    )
    private Long id;

    private String name;

    @CreationTimestamp
    @Column(name = "inserted_at", nullable = false, updatable = false)
    private LocalDateTime insertedAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /* Getters and Setters */
}
