package com.vikram.ems.controller;

import com.vikram.ems.dto.response.EmployeeResponse;
import com.vikram.ems.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    // GET /api/analytics/summary
    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<Map<String, Object>> getSummary() {
        return ResponseEntity.ok(analyticsService.getSummary());
    }

    // GET /api/analytics/by-department
    @GetMapping("/by-department")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<List<Map<String, Object>>> getByDepartment() {
        return ResponseEntity.ok(analyticsService.getCountByDepartment());
    }

    // GET /api/analytics/salary-stats
    @GetMapping("/salary-stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getSalaryStats() {
        return ResponseEntity.ok(analyticsService.getSalaryStats());
    }

    // GET /api/analytics/recent-hires?days=30
    @GetMapping("/recent-hires")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<List<EmployeeResponse>> getRecentHires(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(analyticsService.getRecentHires(days));
    }
}