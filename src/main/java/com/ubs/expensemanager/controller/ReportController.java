package com.ubs.expensemanager.controller;

import java.time.LocalDate;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ubs.expensemanager.service.ReportService;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/expenses")
    public ResponseEntity<?> exportExpenses(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end,
            @RequestParam(defaultValue = "json") String format
    ) {
        if ("csv".equalsIgnoreCase(format)) {
            byte[] bytes = reportService.exportExpensesCsv(start, end);

            String filename = "expenses_" + start + "_to_" + end + ".csv";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.valueOf("text/csv"))
                    .body(bytes);
        }

        // default JSON
        return ResponseEntity.ok(reportService.getExpenseRows(start, end));
    }
}