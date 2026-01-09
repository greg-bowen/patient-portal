package com.bowen.backend.controller;


import com.bowen.backend.model.Request;
import com.bowen.backend.model.Transactions;
import com.bowen.backend.services.EntityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Service
@Slf4j
@RestController
@RequestMapping("/billing")
@RequiredArgsConstructor
public class BillingController {
    private final EntityService entityService;

    @PostMapping(value = "/get/transactions", consumes = "application/json", produces = "application/json")
    public Transactions getTransactions(@RequestBody Request request) {
        log.info("Request: {}", request);
        return entityService.getTransactions(request);
    }
}
