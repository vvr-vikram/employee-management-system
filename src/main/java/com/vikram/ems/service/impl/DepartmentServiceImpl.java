package com.vikram.ems.service.impl;

import com.vikram.ems.dto.request.DepartmentRequest;
import com.vikram.ems.dto.response.DepartmentResponse;
import com.vikram.ems.entity.Department;
import com.vikram.ems.exception.DuplicateResourceException;
import com.vikram.ems.exception.ResourceNotFoundException;
import com.vikram.ems.repository.DepartmentRepository;
import com.vikram.ems.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    public List<DepartmentResponse> findAll() {
        return departmentRepository.findAll()
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentResponse findById(Long id) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
        return toResponse(dept);
    }

    @Override
    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {
        if (departmentRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException(
                    "Department '" + request.getName() + "' already exists");
        }
        Department dept = Department.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        Department saved = departmentRepository.save(dept);
        log.info("Department created: '{}'", saved.getName());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public DepartmentResponse update(Long id, DepartmentRequest request) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));

        if (!dept.getName().equals(request.getName())
                && departmentRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException(
                    "Department '" + request.getName() + "' already exists");
        }

        dept.setName(request.getName());
        dept.setDescription(request.getDescription());
        Department updated = departmentRepository.save(dept);
        log.info("Department updated: id={}", id);
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Department", "id", id);
        }
        departmentRepository.deleteById(id);
        log.info("Department deleted: id={}", id);
    }

    private DepartmentResponse toResponse(Department d) {
        return DepartmentResponse.builder()
                .id(d.getId())
                .name(d.getName())
                .description(d.getDescription())
                .employeeCount(d.getEmployees() != null ? d.getEmployees().size() : 0)
                .createdAt(d.getCreatedAt())
                .build();
    }
}