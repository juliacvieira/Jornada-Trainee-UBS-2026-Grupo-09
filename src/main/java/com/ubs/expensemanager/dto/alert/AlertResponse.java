package com.ubs.expensemanager.dto.alert;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ubs.expensemanager.domain.enums.AlertStatus;
import com.ubs.expensemanager.domain.enums.AlertType;

public record AlertResponse(
        UUID id,
        UUID expenseId,
        AlertType type,
        String message,
        AlertStatus status,
        LocalDateTime createdAt
) {}