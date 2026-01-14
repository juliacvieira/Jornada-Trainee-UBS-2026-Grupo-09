package com.ubs.expensemanager.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ubs.expensemanager.domain.Expense;
import com.ubs.expensemanager.dto.report.ExpenseReportRow;
import com.ubs.expensemanager.repository.ExpenseRepository;

@Service
public class ReportService {

    private final ExpenseRepository expenseRepository;

    public ReportService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public List<ExpenseReportRow> getExpenseRows(LocalDate start, LocalDate end) {
        List<Expense> expenses = expenseRepository.findAllBetweenDates(start, end);

        return expenses.stream().map(e -> new ExpenseReportRow(
                e.getId(),
                e.getDate(),
                e.getEmployee() != null ? e.getEmployee().getName() : null,
                (e.getEmployee() != null && e.getEmployee().getDepartment() != null)
                        ? e.getEmployee().getDepartment().getName()
                        : null,
                e.getCategory() != null ? e.getCategory().getName() : null,
                e.getAmount(),
                e.getCurrency(),
                e.getStatus(),
                e.isNeedsReview()
        )).toList();
    }

    public byte[] exportExpensesCsv(LocalDate start, LocalDate end) {
        List<ExpenseReportRow> rows = getExpenseRows(start, end);

        StringBuilder sb = new StringBuilder();
        sb.append("id,date,employee,department,category,amount,currency,status,needsReview\n");

        for (ExpenseReportRow r : rows) {
            sb.append(csv(r.id()))
                    .append(",").append(csv(r.date()))
                    .append(",").append(csv(r.employeeName()))
                    .append(",").append(csv(r.departmentName()))
                    .append(",").append(csv(r.categoryName()))
                    .append(",").append(csv(r.amount()))
                    .append(",").append(csv(r.currency()))
                    .append(",").append(csv(r.status()))
                    .append(",").append(csv(r.needsReview()))
                    .append("\n");
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String csv(Object v) {
        if (v == null) return "";
        String s = String.valueOf(v);
        boolean needsQuotes = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        if (s.contains("\"")) s = s.replace("\"", "\"\"");
        return needsQuotes ? "\"" + s + "\"" : s;
    }
}
