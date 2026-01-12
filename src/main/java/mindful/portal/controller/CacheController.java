package mindful.portal.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mindful.portal.services.EntityService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
public class CacheController {

    private final EntityService entityService;

    @Cacheable("pronouns")
    @GetMapping(value = "/get-pronouns")
    public List<Map<String, Object>> getTransactions() {
        return entityService.getPronouns();
    }

    @Cacheable("genders")
    @GetMapping(value = "/get-genders")
    public List<Map<String, Object>> getGenders() {
        return entityService.getGenders();
    }

    @Cacheable("phoneTypes")
    @GetMapping(value = "/get-phone-types")
    public List<Map<String, Object>> getPhoneTypes() {
        return entityService.getPhoneTypes();
    }
}
