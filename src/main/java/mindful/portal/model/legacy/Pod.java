package mindful.portal.model.legacy;


import com.vladmihalcea.hibernate.type.array.IntArrayType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "pods", schema = "public")
public class Pod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "state_abbr", columnDefinition = "citext")
    private String stateAbbr;

    @Type(IntArrayType.class)
    @Column(columnDefinition = "integer[]", nullable = false)
    private int[] offices;

    @Type(IntArrayType.class)
    @Column(columnDefinition = "integer[]", nullable = false)
    private int[] providers;

    @CreationTimestamp
    @Column(name = "inserted_at", nullable = false, updatable = false)
    private LocalDateTime insertedAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /* Getters and Setters */
}
