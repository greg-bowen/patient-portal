package mindful.portal.repository;

import lombok.extern.slf4j.Slf4j;
import mindful.portal.model.Email;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailRepositoryOld {
    private final JdbcClient jdbcClient;

    public EmailRepositoryOld(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public boolean insertEmailAddress(int patientId, String email) {
        // set old email to expired
        retireEmail(patientId);

        String sql = "insert into core_bio.emails(patient_id, email) " +
                "values (:patientId, :email)";

        int count = jdbcClient.sql(sql)
                .param("patientId", patientId)
                .param("email", email.trim())
                .update();
        log.info("Inserted row {patient_id: {}, email: {}}", patientId, email);
        return count>0;
    }

    private void retireEmail(int patientId) {
        String sql = "update core_bio.emails " +
                "set expiration_date = current_timestamp " +
                "where patient_id = ? " +
                "and expiration_date is null";
        int count = jdbcClient.sql(sql)
                .params(patientId)
                .update();
        log.info("retired {} row(s) for patient {}", count, patientId);
    }

    public Email getEmail(int patientId) {
        String sql = "select * " +
                "from core_bio.emails " +
                "where patient_id = ? " +
                "and expiration_date is null " +
                "order by seq_id desc " +
                "limit 1";
        return jdbcClient.sql(sql)
                .params(patientId)
                .query(Email.class)
                .single();
    }
}
