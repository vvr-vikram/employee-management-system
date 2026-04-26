package com.vikram.ems.service;

import com.vikram.ems.dto.request.EmployeeRequest;
import com.vikram.ems.dto.response.EmployeeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {
    Page<EmployeeResponse> findAll(Pageable pageable, String status, Long departmentId);
    EmployeeResponse findById(Long id);
    EmployeeResponse create(EmployeeRequest request);
    EmployeeResponse update(Long id, EmployeeRequest request);
    EmployeeResponse updateStatus(Long id, String status);
    void delete(Long id);
    Page<EmployeeResponse> search(String keyword, Pageable pageable);
}