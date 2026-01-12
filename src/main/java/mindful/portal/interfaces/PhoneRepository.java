package mindful.portal.interfaces;

import mindful.portal.model.Phone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhoneRepository extends JpaRepository<Phone, Integer> {

    Optional<Phone> findByPatientIdAndExpirationDateIsNull(long patientId);
}