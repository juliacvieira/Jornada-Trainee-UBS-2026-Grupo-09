package com.ubs.expensemanager.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;

import com.ubs.expensemanager.domain.Alert;
import com.ubs.expensemanager.domain.enums.AlertStatus;
import com.ubs.expensemanager.dto.alert.AlertResponse;
import com.ubs.expensemanager.mapper.AlertMapper;
import com.ubs.expensemanager.service.AlertService;

@RestController
@RequestMapping("/alerts")
public class AlertController {

    private final AlertService service;
    private final AlertMapper mapper;

    public AlertController(AlertService service, AlertMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<AlertResponse> list(@RequestParam(required = false) AlertStatus status) {
        List<Alert> alerts = (status == null) ? service.findAll() : service.findByStatus(status);
        return mapper.toResponseList(alerts);
    }

    @PatchMapping("/{id}/resolve")
    public AlertResponse resolve(@PathVariable UUID id) {
        return mapper.toResponse(service.resolve(id));
    }
}
