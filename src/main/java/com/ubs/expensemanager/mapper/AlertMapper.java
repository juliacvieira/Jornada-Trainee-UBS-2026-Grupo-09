package com.ubs.expensemanager.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ubs.expensemanager.domain.Alert;
import com.ubs.expensemanager.dto.alert.AlertResponse;

@Component
public class AlertMapper {

    public AlertResponse toResponse(Alert alert) {
        return new AlertResponse(
                alert.getId(),
                alert.getExpense() != null ? alert.getExpense().getId() : null,
                alert.getType(),
                alert.getMessage(),
                alert.getStatus(),
                alert.getCreatedAt()
        );
    }

    public List<AlertResponse> toResponseList(List<Alert> alerts) {
        return alerts.stream()
                .map(this::toResponse)
                .toList();
    }
}
