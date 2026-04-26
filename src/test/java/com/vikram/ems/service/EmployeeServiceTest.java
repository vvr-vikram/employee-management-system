package com.vikram.ems.service;

import com.vikram.ems.dto.request.EmployeeRequest;
import com.vikram.ems.dto.response.EmployeeResponse;
import com.vikram.ems.entity.Department;
import com.vikram.ems.entity.Employee;
import com.vikram.ems.entity.EmployeeStatus;
import com.vikram.ems.exception.DuplicateResourceException;
import com.vikram.ems.exception.ResourceNotFoundException;
import com.vikram.ems.repository.DepartmentRepository;
import com.vikram.ems.repository.EmployeeRepository;
import com.vikram.ems.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock private EmployeeRepository employeeRepository;
    @Mock private DepartmentRepository departmentRepository;

    @InjectMocks private EmployeeServiceImpl employeeService;

    private Employee sampleEmployee;
    private Department sampleDepartment;

    @BeforeEach
    void setUp() {
        sampleDepartment = Department.builder()
                .id(1L).name("Engineering").build();

        sampleEmployee = Employee.builder()
                .id(1L)
                .firstName("Vikram")
                .lastName("Kumar")
                .email("vikram@ems.com")
                .phone("9876543210")
                .salary(new BigDecimal("75000"))
                .hireDate(LocalDate.of(2023, 1, 15))
                .status(EmployeeStatus.ACTIVE)
                .department(sampleDepartment)
                .build();
    }

    // -------- findById --------

    @Test
    @DisplayName("findById - should return response when employee exists")
    void findById_success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(sampleEmployee));

        EmployeeResponse response = employeeService.findById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getFirstName()).isEqualTo("Vikram");
        assertThat(response.getEmail()).isEqualTo("vikram@ems.com");
        assertThat(response.getDepartmentName()).isEqualTo("Engineering");
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById - should throw ResourceNotFoundException when not found")
    void findById_notFound() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Employee");
    }

    // -------- create --------

    @Test
    @DisplayName("create - should save and return employee response")
    void create_success() {
        EmployeeRequest request = EmployeeRequest.builder()
                .firstName("Priya").lastName("Sharma")
                .email("priya@ems.com").phone("9876543211")
                .salary(new BigDecimal("65000"))
                .hireDate(LocalDate.now())
                .departmentId(1L)
                .build();

        when(employeeRepository.existsByEmail("priya@ems.com")).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(sampleDepartment));
        when(employeeRepository.save(any(Employee.class))).thenReturn(sampleEmployee);

        EmployeeResponse response = employeeService.create(request);

        assertThat(response).isNotNull();
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    @DisplayName("create - should throw DuplicateResourceException for duplicate email")
    void create_duplicateEmail() {
        EmployeeRequest request = EmployeeRequest.builder()
                .firstName("Test").lastName("User")
                .email("vikram@ems.com")
                .departmentId(1L)
                .build();

        when(employeeRepository.existsByEmail("vikram@ems.com")).thenReturn(true);

        assertThatThrownBy(() -> employeeService.create(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("vikram@ems.com");

        verify(employeeRepository, never()).save(any());
    }

    @Test
    @DisplayName("create - should throw ResourceNotFoundException when department not found")
    void create_departmentNotFound() {
        EmployeeRequest request = EmployeeRequest.builder()
                .firstName("Test").lastName("User")
                .email("new@ems.com")
                .departmentId(999L)
                .build();

        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Department");
    }

    // -------- update --------

    @Test
    @DisplayName("update - should update and return updated employee")
    void update_success() {
        EmployeeRequest request = EmployeeRequest.builder()
                .firstName("Vikram").lastName("Updated")
                .email("vikram@ems.com").phone("9876543210")
                .salary(new BigDecimal("80000"))
                .hireDate(LocalDate.now())
                .departmentId(1L)
                .build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(sampleEmployee));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(sampleDepartment));
        when(employeeRepository.save(any(Employee.class))).thenReturn(sampleEmployee);

        EmployeeResponse response = employeeService.update(1L, request);

        assertThat(response).isNotNull();
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    @DisplayName("update - should throw ResourceNotFoundException when employee not found")
    void update_notFound() {
        EmployeeRequest request = EmployeeRequest.builder()
                .email("vikram@ems.com").departmentId(1L).build();

        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.update(99L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // -------- updateStatus --------

    @Test
    @DisplayName("updateStatus - should change status to ON_LEAVE")
    void updateStatus_success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(sampleEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(sampleEmployee);

        EmployeeResponse response = employeeService.updateStatus(1L, "ON_LEAVE");

        assertThat(response).isNotNull();
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    @DisplayName("updateStatus - should throw for invalid status value")
    void updateStatus_invalidStatus() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(sampleEmployee));

        assertThatThrownBy(() -> employeeService.updateStatus(1L, "INVALID_STATUS"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid status");
    }

    // -------- delete --------

    @Test
    @DisplayName("delete - should soft-delete (set status INACTIVE)")
    void delete_success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(sampleEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(sampleEmployee);

        employeeService.delete(1L);

        verify(employeeRepository).save(argThat(e ->
                e.getStatus() == EmployeeStatus.INACTIVE));
    }

    @Test
    @DisplayName("delete - should throw when employee not found")
    void delete_notFound() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(employeeRepository, never()).save(any());
    }

    // -------- findAll --------

    @Test
    @DisplayName("findAll - should return paginated result")
    void findAll_noFilters() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        Page<Employee> page = new PageImpl<>(List.of(sampleEmployee));

        when(employeeRepository.findAll(pageable)).thenReturn(page);

        Page<EmployeeResponse> result = employeeService.findAll(pageable, null, null);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("Vikram");
    }
}