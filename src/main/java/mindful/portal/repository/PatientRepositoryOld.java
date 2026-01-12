package mindful.portal.repository;

import lombok.extern.slf4j.Slf4j;
import mindful.portal.model.PatientInfo;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class PatientRepositoryOld {
    private final JdbcClient jdbcClient;
    public PatientRepositoryOld(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public PatientInfo getPatientInfo(int patientId) {
        String sql = "select p.*, e.email " +
                "from core_bio.patients p, core_bio.emails e " +
                "where p.patient_id = e.patient_id " +
                "and e.expiration_date is null " +
                "and p.patient_id = :patientId " +
                "order by e.seq_id desc " +
                "limit 1";
        try {
            PatientInfo result = jdbcClient.sql(sql)
                    .param("patientId", patientId)
                    .query(PatientInfo.class)
                    .single();
            log.info("Patient found: {}", result);
            return result;
        }
        catch(EmptyResultDataAccessException e){
            log.info("No patient found with id: {}", patientId);
            throw new RuntimeException("No patient found with id: " + patientId);
        }
    }

    public List<Map<String, Object>> getGenders(){
        return jdbcClient.sql("select id, name " +
                        "from core_bio.genders")
                .query()
                .listOfRows();
    }

    public List<Map<String, Object>> getPronouns(){
        return jdbcClient.sql("select id, name " +
                        "from core_bio.pronouns ")
                .query()
                .listOfRows();
    }
}

