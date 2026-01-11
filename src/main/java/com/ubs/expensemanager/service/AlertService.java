package com.ubs.expensemanager.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ubs.expensemanager.domain.Alert;
import com.ubs.expensemanager.domain.Expense;
import com.ubs.expensemanager.domain.enums.AlertStatus;
import com.ubs.expensemanager.domain.enums.AlertType;
import com.ubs.expensemanager.exception.BusinessException;
import com.ubs.expensemanager.repository.AlertRepository;

@Service
public class AlertService {

    private final AlertRepository repository;

    public AlertService(AlertRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Alert createAlert(Expense expense, AlertType type, String message) {
        Alert alert = new Alert();
        alert.setExpense(expense);
        alert.setType(type);
        alert.setMessage(message);
        alert.setStatus(AlertStatus.NEW);
        alert.setCreatedAt(LocalDateTime.now());
        return repository.save(alert);
    }

    public List<Alert> findAll() {
        return repository.findAll();
    }

    public List<Alert> findByStatus(AlertStatus status) {
        return repository.findByStatus(status);
    }

    @Transactional
    public Alert resolve(UUID id) {
        Alert alert = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Alert not found"));

        alert.setStatus(AlertStatus.RESOLVED);
        return repository.save(alert);
    }
}
