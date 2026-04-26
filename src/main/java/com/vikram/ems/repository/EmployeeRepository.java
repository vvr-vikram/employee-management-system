package com.vikram.ems.repository;

import com.vikram.ems.entity.Employee;
import com.vikram.ems.entity.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>,
        JpaSpecificationExecutor<Employee> {

    Boolean existsByEmail(String email);

    long countByStatus(EmployeeStatus status);

    List<Employee> findByHireDateAfter(LocalDate date);

    Page<Employee> findByStatus(EmployeeStatus status, Pageable pageable);

    Page<Employee> findByDepartmentId(Long departmentId, Pageable pageable);

    // Count employees per department
    @Query("SELECT e.department.name, COUNT(e) FROM Employee e " +
           "GROUP BY e.department.name ORDER BY COUNT(e) DESC")
    List<Object[]> countByDepartmentRaw();

    // Salary stats per department (active employees only)
    @Query("SELECT e.department.name, AVG(e.salary), MIN(e.salary), MAX(e.salary) " +
           "FROM Employee e WHERE e.status = 'ACTIVE' GROUP BY e.department.name")
    List<Object[]> salaryStatsByDepartmentRaw();

    // Full text search by name or email
    @Query("SELECT e FROM Employee e WHERE " +
           "LOWER(e.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.lastName)  LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.email)     LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Employee> searchByKeyword(String keyword, Pageable pageable);
}