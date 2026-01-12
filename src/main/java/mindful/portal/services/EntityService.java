package mindful.portal.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mindful.portal.interfaces.AddressRepository;
import mindful.portal.interfaces.EmailRepository;
import mindful.portal.interfaces.PatientInfoRepository;
import mindful.portal.interfaces.PhoneRepository;
import mindful.portal.model.*;
import mindful.portal.repository.PatientRepositoryOld;
import mindful.portal.repository.PhoneRepositoryOld;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EntityService {

    private final PatientInfoRepository patientInfoRepository;
    private final EmailRepository emailRepo;
    private final AddressRepository addressRepo;
    private final PhoneRepository phoneRepo;

    private final PatientRepositoryOld getters;
    private final PhoneRepositoryOld phoneRepositoryOld;

    public Patient getPatient(int patientId) {

        PatientInfo info = patientInfoRepository.findByPatientId(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));

        Email email = emailRepo.findByPatientIdAndExpirationDateIsNull(patientId).orElse(null);
        Address address = addressRepo.findByPatientIdAndExpirationDateIsNull(patientId).orElse(null);
        Phone phone = phoneRepo.findByPatientIdAndExpirationDateIsNull(patientId).orElse(null);

        Patient patient = new Patient();
        patient.setPatientInfo(info);
        patient.setEmail(email);
        patient.setAddress(address);
        patient.setPhone(phone);

        return patient;
    }

    public void savePatient(PatientInfo patient) {
        patientInfoRepository.save(patient);
    }

    public List<Map<String, Object>> getPronouns() {
        return getters.getPronouns();
    }

    public List<Map<String, Object>> getGenders() {
        return getters.getGenders();
    }

    public List<Map<String, Object>> getPhoneTypes() {
        return phoneRepositoryOld.getPhoneTypes();
    }

    public void savePhone(Phone phone) {
        phoneRepo.save(phone);
    }

    public void saveAddress(Address address) {
        addressRepo.save(address);
    }

    public void saveEmail(Email email) {
        emailRepo.save(email);
    }
}
