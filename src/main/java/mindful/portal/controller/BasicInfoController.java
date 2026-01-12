package mindful.portal.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mindful.portal.model.Patient;
import mindful.portal.model.PatientInfo;
import mindful.portal.model.Phone;
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
        Patient patient = entityService.getPatient(id);
        log.info("response: {}", objectMapper.writeValueAsString(patient));
        return patient;
    }

    @PatchMapping(value = "/update-personal-info", consumes = "application/json")
    public String updatePersonalInfo(@RequestBody Patient request) throws JsonProcessingException {
        log.info("Request: {}", objectMapper.writeValueAsString(request));
        entityService.savePatient(request.getPatientInfo());
        return "{\"valid\": true}";
    }

    @PatchMapping(value = "/update-phone", consumes = "application/json")
    public String updatePhone(@RequestBody String request) {
        log.info("Request: {}", request);
//        entityService.savePatient(request.getPatientInfo());
        return "{\"valid\": true}";
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
