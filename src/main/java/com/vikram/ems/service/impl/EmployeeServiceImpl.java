package com.vikram.ems.service.impl;

import com.vikram.ems.dto.request.EmployeeRequest;
import com.vikram.ems.dto.response.EmployeeResponse;
import com.vikram.ems.entity.Department;
import com.vikram.ems.entity.Employee;
import com.vikram.ems.entity.EmployeeStatus;
import com.vikram.ems.exception.DuplicateResourceException;
import com.vikram.ems.exception.ResourceNotFoundException;
import com.vikram.ems.repository.DepartmentRepository;
import com.vikram.ems.repository.EmployeeRepository;
import com.vikram.ems.service.EmployeeService;
import com.vikram.ems.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public Page<EmployeeResponse> findAll(Pageable pageable, String status, Long departmentId) {
        Page<Employee> employees;

        if (status != null && departmentId != null) {
            EmployeeStatus empStatus = parseStatus(status);
            employees = employeeRepository.findByStatus(empStatus, pageable);
        } else if (status != null) {
            EmployeeStatus empStatus = parseStatus(status);
            employees = employeeRepository.findByStatus(empStatus, pageable);
        } else if (departmentId != null) {
            employees = employeeRepository.findByDepartmentId(departmentId, pageable);
        } else {
            employees = employeeRepository.findAll(pageable);
        }

        return employees.map(this::toResponse);
    }

    @Override
    public EmployeeResponse findById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        return toResponse(employee);
    }

    @Override
    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Employee with email '" + request.getEmail() + "' already exists");
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department", "id", request.getDepartmentId()));

        Employee employee = Employee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .salary(request.getSalary())
                .hireDate(request.getHireDate())
                .status(EmployeeStatus.ACTIVE)
                .department(department)
                .createdBy(SecurityUtil.getCurrentUser())
                .build();

        Employee saved = employeeRepository.save(employee);
        log.info("Employee created with id: {}", saved.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        // Check email duplicate only if changed
        if (!employee.getEmail().equals(request.getEmail())
                && employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Email '" + request.getEmail() + "' is already in use");
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department", "id", request.getDepartmentId()));

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setSalary(request.getSalary());
        employee.setHireDate(request.getHireDate());
        employee.setDepartment(department);

        Employee updated = employeeRepository.save(employee);
        log.info("Employee updated: id={}", id);
        return toResponse(updated);
    }

    @Override
    @Transactional
    public EmployeeResponse updateStatus(Long id, String status) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        employee.setStatus(parseStatus(status));
        Employee updated = employeeRepository.save(employee);
        log.info("Employee status updated: id={}, status={}", id, status);
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        // Soft delete: set status to INACTIVE
        employee.setStatus(EmployeeStatus.INACTIVE);
        employeeRepository.save(employee);
        log.info("Employee soft-deleted: id={}", id);
    }

    @Override
    public Page<EmployeeResponse> search(String keyword, Pageable pageable) {
        return employeeRepository.searchByKeyword(keyword, pageable)
                .map(this::toResponse);
    }

    // ---------- Mapper ----------

    private EmployeeResponse toResponse(Employee e) {
        return EmployeeResponse.builder()
                .id(e.getId())
                .firstName(e.getFirstName())
                .lastName(e.getLastName())
                .email(e.getEmail())
                .phone(e.getPhone())
                .salary(e.getSalary())
                .hireDate(e.getHireDate())
                .status(e.getStatus())
                .departmentId(e.getDepartment() != null ? e.getDepartment().getId() : null)
                .departmentName(e.getDepartment() != null ? e.getDepartment().getName() : null)
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    private EmployeeStatus parseStatus(String status) {
        try {
            return EmployeeStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid status: " + status + ". Valid values: ACTIVE, INACTIVE, ON_LEAVE");
        }
    }
}