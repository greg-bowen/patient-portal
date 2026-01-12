package mindful.portal.interfaces;

import mindful.portal.model.Phone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PhoneRepository extends JpaRepository<Phone, Integer> {

    Optional<List<Phone>> findByPatientId(long patientId);
}