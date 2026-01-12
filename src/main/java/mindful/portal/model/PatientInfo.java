package mindful.portal.model;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(schema = "core_bio", name = "patients")
public class PatientInfo {
    @Id
    private long patientId;

    private String preferredName;
    private String firstName;
    private String middleName;
    private String lastName;
    private LocalDate dob;
    private String gender;
    private Integer pronouns;

//    @Override
//    public String toString() {
//        try {
//            return new ObjectMapper().disable(MapperFeature.REQUIRE_HANDLERS_FOR_JAVA8_TIMES).writerWithDefaultPrettyPrinter().writeValueAsString(this);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
