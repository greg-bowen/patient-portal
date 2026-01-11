package mindful.portal.controller;


import mindful.portal.model.Patient;
import mindful.portal.model.Request;
import mindful.portal.services.EntityService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class BasicInfoController {

    private final EntityService entityService;

    public BasicInfoController(EntityService entityService) {
        this.entityService = entityService;
    }

    @GetMapping(value = "/get-patient")
    public Patient getPatient(@RequestParam int id) {
        Patient patient = entityService.getPatient(id);
        log.info("Patient: {}", patient);
        return patient;
    }

    @PostMapping(value = "/insert-phone", consumes = "application/json")
    public String insertPhone(@RequestBody Request request) {
        entityService.insertPhone(request.getPatientId(), request.getPhone());
        return "{\"valid\": true}";
    }

    @PostMapping(value = "/insert-email", consumes = "application/json")
    public String insertEmail(@RequestBody JSONObject request) {
        entityService.insertEmailAddress(request.getInt("patientId"), request.getString("email"));
        return "{\"valid\": true}";
    }

    @PostMapping(value = "/insert-address", consumes = "application/json")
    public String updateAddress(@RequestBody Request request) {
        entityService.insertAddress(request.getPatientId(), request.getAddress());
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
