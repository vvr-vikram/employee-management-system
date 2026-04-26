package com.vikram.ems.service.impl;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.vikram.ems.dto.response.EmployeeResponse;
import com.vikram.ems.entity.Employee;
import com.vikram.ems.entity.EmployeeStatus;
import com.vikram.ems.repository.DepartmentRepository;
import com.vikram.ems.repository.EmployeeRepository;
import com.vikram.ems.service.AnalyticsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsServiceImpl implements AnalyticsService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public Map<String, Object> getSummary() {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalEmployees",    employeeRepository.count());
        summary.put("activeEmployees",   employeeRepository.countByStatus(EmployeeStatus.ACTIVE));
        summary.put("inactiveEmployees", employeeRepository.countByStatus(EmployeeStatus.INACTIVE));
        summary.put("onLeaveEmployees",  employeeRepository.countByStatus(EmployeeStatus.ON_LEAVE));
        summary.put("totalDepartments",  departmentRepository.count());
        return summary;
    }

    @Override
    public List<Map<String, Object>> getCountByDepartment() {
        List<Object[]> raw = employeeRepository.countByDepartmentRaw();
        return raw.stream().map(row -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("department", row[0]);
            map.put("count", row[1]);
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getSalaryStats() {
        List<Object[]> raw = employeeRepository.salaryStatsByDepartmentRaw();
        return raw.stream().map(row -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("department",     row[0]);
            map.put("averageSalary",  row[1] != null ? ((Double) row[1]).longValue() : 0);
            map.put("minSalary",      row[2]);
            map.put("maxSalary",      row[3]);
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<EmployeeResponse> getRecentHires(int days) {
        LocalDate since = LocalDate.now().minusDays(days);
        return employeeRepository.findByHireDateAfter(since)
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
    }

    private EmployeeResponse toResponse(Employee e) {
        return EmployeeResponse.builder()
                .id(e.getId())
                .firstName(e.getFirstName())
                .lastName(e.getLastName())
                .email(e.getEmail())
                .hireDate(e.getHireDate())
                .status(e.getStatus())
                .departmentName(e.getDepartment() != null ? e.getDepartment().getName() : null)
                .build();
    }
}