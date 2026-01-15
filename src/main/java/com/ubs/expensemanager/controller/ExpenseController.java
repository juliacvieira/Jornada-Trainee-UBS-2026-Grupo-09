package com.ubs.expensemanager.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;

import com.ubs.expensemanager.service.ExpenseService;
import com.ubs.expensemanager.domain.Expense;
import com.ubs.expensemanager.dto.expense.CreateExpenseRequest;
import com.ubs.expensemanager.dto.expense.ExpenseResponse;
import com.ubs.expensemanager.dto.expense.UpdateExpenseRequest;
import com.ubs.expensemanager.mapper.ExpenseMapper;
import com.ubs.expensemanager.exception.BusinessException;

@RestController
@RequestMapping("/expense")
public class ExpenseController {

    private final ExpenseService service;
    private final ExpenseMapper mapper;

    public ExpenseController(ExpenseService service, ExpenseMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<ExpenseResponse> getExpenses() {
        List<Expense> expenses = service.findAll();
        return mapper.toResponseList(expenses);
    }

    @GetMapping("/{id}")
    public ExpenseResponse getExpensesById(@PathVariable UUID id) {
        Expense expense = service.findById(id);
        return ExpenseMapper.toResponse(expense);
    }

    @PostMapping
    public ExpenseResponse newExpense(@RequestBody CreateExpenseRequest request) {
        Expense expense = service.createExpense(request);
        return ExpenseMapper.toResponse(expense);
    }

    @PatchMapping("/{id}")
    public ExpenseResponse updateExpense(@PathVariable UUID id, @RequestBody UpdateExpenseRequest request) {
        Expense expense = service.updateExpense(id, request);
        return ExpenseMapper.toResponse(expense);
    }

    @PostMapping("/{id}/receipt")
    public ExpenseResponse uploadReceipt(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        Expense updated = service.attachReceipt(id, file);
        return ExpenseMapper.toResponse(updated);
    }

    @GetMapping("/{id}/receipt")
    @SuppressWarnings("null")
    public ResponseEntity<Resource> downloadReceipt(@PathVariable UUID id) {
        Resource resource = service.loadReceipt(id);

        // best-effort filename
        String filename = "receipt";
        if (resource.getFilename() != null) {
            filename = resource.getFilename();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
    @PostMapping("/{id}/approve/manager")
    @PreAuthorize("hasRole('MANAGER')")
    public ExpenseResponse approveByManager(@PathVariable UUID id, Authentication auth) {
        UUID managerId = extractUserId(auth);
        Expense updated = service.approveByManager(id, managerId);
        return mapper.toResponse(updated);
    }

    @PostMapping("/{id}/approve/finance")
    @PreAuthorize("hasRole('FINANCE')")
    public ExpenseResponse approveByFinance(@PathVariable UUID id, Authentication auth) {
        UUID financeId = extractUserId(auth);
        Expense updated = service.approveByFinance(id, financeId);
        return mapper.toResponse(updated);
    }

    public static record RejectRequest(String reason) {}

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('MANAGER','FINANCE')")
    public ExpenseResponse reject(@PathVariable UUID id, @RequestBody RejectRequest body, Authentication auth) {
        UUID userId = extractUserId(auth);
        Expense updated = service.rejectExpense(id, userId, body == null ? null : body.reason());
        return mapper.toResponse(updated);
    }

    /**
     * Tries to extract a UUID user id from Authentication#getName().
     * If your authentication stores the user id elsewhere (e.g. as a claim or principal),
     * adjust this method accordingly.
     */
    private UUID extractUserId(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            throw new BusinessException("Unauthenticated request");
        }

        String name = auth.getName();
        try {
            return UUID.fromString(name);
        } catch (IllegalArgumentException ex) {
            // If your principal is not a UUID, try other strategies here (e.g. cast principal to custom UserDetails)
            // For now, be explicit so you quickly notice and update.
            throw new BusinessException("Cannot extract user id from authentication; expected UUID in auth.getName()");
        }
    }
}
