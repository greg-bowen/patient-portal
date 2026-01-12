package mindful.portal.model.legacy;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "payers", schema = "public")
public class Payer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String code;

    @Column(name = "pverify_code")
    private String pverifyCode;

    @Column(name = "state_abbr", columnDefinition = "citext")
    private String stateAbbr;

    @CreationTimestamp
    @Column(name = "inserted_at", nullable = false, updatable = false)
    private LocalDateTime insertedAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "pverify_name")
    private String pverifyName;

    @Column(name = "arboost_code")
    private String arboostCode;

    /* Getters and Setters omitted for brevity */
}
