package mindful.portal.model.legacy;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "users", schema = "public")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "citext")
    private String email;

    private LocalDate dob;

    @Column(name = "hashed_password")
    private String hashedPassword;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String username;

    @Column(name = "stripe_id")
    private String stripeId;

    @Column(name = "image_path")
    private String imagePath;

    private String phone;

    @Column(name = "state_abbr", columnDefinition = "citext")
    private String stateAbbr;

    @Column(name = "drchrono_oauth_refresh_token")
    private String drchronoOauthRefreshToken;

    @Column(name = "drchrono_uuid")
    private Long drchronoUuid;

    @Column(name = "expected_insurance")
    private String expectedInsurance;

    @Column(name = "consent_url")
    private String consentUrl;

    private String gender;

    private Boolean preexisted;

    private String address;

    private String city;

    @Column(name = "zip_code")
    private String zipCode;

    @CreationTimestamp
    @Column(name = "inserted_at", nullable = false, updatable = false)
    private LocalDateTime insertedAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_id")
    private Payer payer;

    @Column(name = "time_zone")
    private String timeZone;

    @Column(name = "allow_to_see_dashboard")
    private Boolean allowToSeeDashboard = false;

    @Column(name = "invite_code")
    private String inviteCode;

    @Column(name = "gfe_generated_at")
    private LocalDateTime gfeGeneratedAt;

    @Column(name = "gfe_pdf_url")
    private String gfePdfUrl;

    @Column(name = "contact_consent")
    private Boolean contactConsent = false;

    @Column(name = "custom_payer_name")
    private String customPayerName;

    @Column(name = "imported_from_drchrono")
    private Boolean importedFromDrchrono = false;

    @Column(name = "snowflake_oauth_refresh_token", columnDefinition = "text")
    private String snowflakeOauthRefreshToken;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private UserRole role;

    @Column(name = "public_id")
    private UUID publicId;

    @Column(name = "first_scheduled_appointment")
    private LocalDateTime firstScheduledAppointment;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pod_id")
    private Pod pod;

    /* Getters and Setters omitted for brevity */
}
