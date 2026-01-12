package mindful.portal.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class PatientRepositoryOld {
    private final JdbcClient jdbcClient;

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

