package mindful.portal.repository;


import lombok.extern.slf4j.Slf4j;
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

    public List<Map<String, Object>> getPhoneTypes(){
        return jdbcClient.sql("select code, description " +
                        "from core_bio.phone_type ")
                .query()
                .listOfRows();
    }
}
