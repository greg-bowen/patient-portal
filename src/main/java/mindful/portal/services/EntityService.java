package mindful.portal.services;

import mindful.portal.model.*;
import mindful.portal.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class EntityService {

    private final PatientRepository patientRepository;
    private final AddressRepository addressRepository;
    private final EmailRepository emailRepository;
    private final PhoneRepository phoneRepository;
    private final TransactionRepository transactionRepository;

    public EntityService(PatientRepository patientRepository, AddressRepository addressRepository, EmailRepository emailRepository, PhoneRepository phoneRepository, TransactionRepository transactionRepository) {
        this.patientRepository = patientRepository;
        this.addressRepository = addressRepository;
        this.emailRepository = emailRepository;
        this.phoneRepository = phoneRepository;
        this.transactionRepository = transactionRepository;
    }

    public void insertPhone(int patientId, Phone phone) {
        phoneRepository.insertPhone(patientId, phone);
    }

    public void insertEmailAddress(int patientId, String email) {
        emailRepository.insertEmailAddress(patientId, email);
    }

    public void insertAddress(int patientId, Address address) {
        addressRepository.insertAddress(patientId, address);
    }

    public Patient getPatient(int patientId) {
        Patient patient = patientRepository.getPatientInfo(patientId);
        patient.setAddress(addressRepository.getAddresses(patientId));
        patient.setPhone(phoneRepository.getPhone(patientId));
        return patient;
    }

    public Transactions getTransactions(Request request) {
        return new Transactions(transactionRepository.getTransactions(request.getPatientId()));
    }

    public List<Map<String, Object>> getPronouns() {
        return patientRepository.getPronouns();
    }

    public List<Map<String, Object>> getGenders() {
        return patientRepository.getGenders();
    }

    public List<Map<String, Object>> getPhoneTypes() {
        return phoneRepository.getPhoneTypes();
    }
}
