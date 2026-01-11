package mindful.portal.repository;


import mindful.portal.model.Address;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AddressRepository {

    private final JdbcClient jdbcClient;
    public AddressRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void insertAddress(int patientId, Address address) {
    }

    public List<Address> getAddresses(int patientId) {
        try {
            List<Address> result = jdbcClient.sql("select * " +
                            "from core_bio.addresses " +
                            "where patient_id = :patientId " +
                            "and expiration_date is null " +
                            "order by seq_id desc " +
                            "limit 1")
                    .param("patientId", patientId)
                    .query(Address.class)
                    .list();
            log.info("Addresses found: {}", result);
            return result;
        } catch (EmptyResultDataAccessException e) {
            log.info("No address found with id: {}", patientId);
            throw new RuntimeException("No address found with id: " + patientId);
        }
    }
}
