package com.ubs.expensemanager.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ubs.expensemanager.domain.Alert;
import com.ubs.expensemanager.domain.enums.AlertStatus;

@Repository
public interface AlertRepository extends JpaRepository<Alert, UUID> {

    List<Alert> findByStatus(AlertStatus status);

    List<Alert> findByExpenseId(UUID expenseId);
}