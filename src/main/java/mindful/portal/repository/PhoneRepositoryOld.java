package mindful.portal.repository;


import lombok.extern.slf4j.Slf4j;
import mindful.portal.model.Phone;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PhoneRepositoryOld {
    private final JdbcClient jdbcClient;

    public PhoneRepositoryOld(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void insertPhone(int patientId, Phone phone) {
        // set old phone to expired
        retirePhone(patientId, String.valueOf(phone.getType()));

        String sql = "insert into core_bio.phone(patient_id, phone_number, type) " +
                "values (:patientId, :phone, :type)";

        jdbcClient.sql(sql)
                .param("patientId", patientId)
                .param("phone", phone.getPhoneNumber().replaceAll("[^0-9]", "").trim())
                .param("type", phone.getType())
                .update();
        log.info("Inserted row {patient_id: {}, phone: {}, type: {}}", patientId, phone, phone.getType());
    }

    private void retirePhone(int patientId, String type) {
        String sql = "update core_bio.phone " +
                "set expiration_date = current_timestamp " +
                "where patient_id = ? " +
                "and type = ? " +
                "and expiration_date is null";
        int count = jdbcClient.sql(sql)
                .params(patientId)
                .params(type)
                .update();
        log.info("retired {} row(s) for patient {}", count, patientId);
    }

    public List<Phone> getPhone(int patientId) {
        return jdbcClient.sql("select * " +
                        "from core_bio.phone " +
                        "where patient_id = ? " +
                        "and expiration_date is null " +
                        "order by seq_id desc")
                .params(patientId)
                .query(Phone.class)
                .list();
    }

    public List<Map<String, Object>> getPhoneTypes(){
        return jdbcClient.sql("select code, description " +
                        "from core_bio.phone_type ")
                .query()
                .listOfRows();
    }
}
