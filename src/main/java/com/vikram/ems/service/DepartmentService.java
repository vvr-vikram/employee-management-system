package com.vikram.ems.service;

import com.vikram.ems.dto.request.DepartmentRequest;
import com.vikram.ems.dto.response.DepartmentResponse;

import java.util.List;

public interface DepartmentService {
    List<DepartmentResponse> findAll();
    DepartmentResponse findById(Long id);
    DepartmentResponse create(DepartmentRequest request);
    DepartmentResponse update(Long id, DepartmentRequest request);
    void delete(Long id);
}