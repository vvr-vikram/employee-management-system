package com.vikram.ems.controller;

import com.vikram.ems.dto.request.EmployeeRequest;
import com.vikram.ems.dto.response.EmployeeResponse;
import com.vikram.ems.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    // GET /api/employees?page=0&size=10&sortBy=id&status=ACTIVE&departmentId=1
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','HR','VIEWER')")
    public ResponseEntity<Page<EmployeeResponse>> getAll(
            @RequestParam(defaultValue = "0")     int page,
            @RequestParam(defaultValue = "10")    int size,
            @RequestParam(defaultValue = "id")    String sortBy,
            @RequestParam(required = false)       String status,
            @RequestParam(required = false)       Long departmentId) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(employeeService.findAll(pageable, status, departmentId));
    }

    // GET /api/employees/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR','VIEWER')")
    public ResponseEntity<EmployeeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.findById(id));
    }

    // GET /api/employees/search?keyword=vikram
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<Page<EmployeeResponse>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(employeeService.search(keyword, pageable));
    }

    // POST /api/employees
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<EmployeeResponse> create(
            @Valid @RequestBody EmployeeRequest request) {

        EmployeeResponse created = employeeService.create(request);
        URI location = URI.create("/api/employees/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    // PUT /api/employees/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<EmployeeResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(employeeService.update(id, request));
    }

    // PATCH /api/employees/{id}/status?status=ON_LEAVE
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(employeeService.updateStatus(id, status));
    }

    // DELETE /api/employees/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}