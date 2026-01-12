package mindful.portal.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mindful.portal.model.Patient;
import mindful.portal.services.EntityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BasicInfoController {

    private final EntityService entityService;
    private final ObjectMapper objectMapper;

    @GetMapping(value = "/get-patient")
    public Patient getPatient(@RequestParam int id) throws JsonProcessingException {
        log.info("request: get-patient {}", id);
        Patient patient = entityService.getPatient(id);
        log.info("response: {}", objectMapper.writeValueAsString(patient));
        return patient;
    }

    @PatchMapping(value = "/update-personal-info", consumes = "application/json")
    public void updatePersonalInfo(@RequestBody Patient request) {
        entityService.savePatient(request.getPatientInfo());
    }

    @PatchMapping(value = "/update-phone", consumes = "application/json")
    public void updatePhone(@RequestBody Patient request) {
        entityService.savePhone(request.getPhone());
    }

    @PatchMapping(value = "/update-address", consumes = "application/json")
    public void updateAddress(@RequestBody Patient request) {
        entityService.saveAddress(request.getAddress());
    }

    @PatchMapping(value = "/update-email", consumes = "application/json")
    public void updateEmail(@RequestBody Patient request) {
        entityService.saveEmail(request.getEmail());
    }

    @GetMapping(value = "/get-pronouns")
    public List<Map<String, Object>> getTransactions() {
        return entityService.getPronouns();
    }

    @GetMapping(value = "/get-genders")
    public List<Map<String, Object>> getGenders() {
        return entityService.getGenders();
    }

    @GetMapping(value = "/get-phone-types")
    public List<Map<String, Object>> getPhoneTypes() {
        return entityService.getPhoneTypes();
    }
}
