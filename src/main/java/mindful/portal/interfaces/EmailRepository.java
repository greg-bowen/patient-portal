package mindful.portal.interfaces;

import mindful.portal.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailRepository extends JpaRepository<Email, Integer> {

    Optional<Email> findByPatientId(long patientId);
}