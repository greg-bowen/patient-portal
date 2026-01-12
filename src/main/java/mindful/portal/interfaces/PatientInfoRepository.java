package mindful.portal.interfaces;

import mindful.portal.model.PatientInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientInfoRepository extends JpaRepository<PatientInfo, Long> {

    Optional<PatientInfo> findByPatientId(long patientId);
}