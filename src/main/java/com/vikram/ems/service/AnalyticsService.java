package com.vikram.ems.service;

import com.vikram.ems.dto.response.EmployeeResponse;

import java.util.List;
import java.util.Map;

public interface AnalyticsService {
    Map<String, Object> getSummary();
    List<Map<String, Object>> getCountByDepartment();
    List<Map<String, Object>> getSalaryStats();
    List<EmployeeResponse> getRecentHires(int days);
}