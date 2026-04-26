package com.vikram.ems.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Dashboard summary
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class DashboardSummaryResponse {
    private long totalEmployees;
    private long activeEmployees;
    private long inactiveEmployees;
    private long onLeaveEmployees;
    private long totalDepartments;
	public long getTotalEmployees() {
		return totalEmployees;
	}
	public void setTotalEmployees(long totalEmployees) {
		this.totalEmployees = totalEmployees;
	}
	public long getActiveEmployees() {
		return activeEmployees;
	}
	public void setActiveEmployees(long activeEmployees) {
		this.activeEmployees = activeEmployees;
	}
	public long getInactiveEmployees() {
		return inactiveEmployees;
	}
	public void setInactiveEmployees(long inactiveEmployees) {
		this.inactiveEmployees = inactiveEmployees;
	}
	public long getOnLeaveEmployees() {
		return onLeaveEmployees;
	}
	public void setOnLeaveEmployees(long onLeaveEmployees) {
		this.onLeaveEmployees = onLeaveEmployees;
	}
	public long getTotalDepartments() {
		return totalDepartments;
	}
	public void setTotalDepartments(long totalDepartments) {
		this.totalDepartments = totalDepartments;
	}
    
}

// Department employee count
@Data
@NoArgsConstructor
@AllArgsConstructor
class DeptEmployeeCountResponse {
    private String departmentName;
    private long employeeCount;
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public long getEmployeeCount() {
		return employeeCount;
	}
	public void setEmployeeCount(long employeeCount) {
		this.employeeCount = employeeCount;
	}
    
}

// Salary stats per department
@Data
@NoArgsConstructor
@AllArgsConstructor
class SalaryStatsResponse {
    private String departmentName;
    private BigDecimal averageSalary;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public BigDecimal getAverageSalary() {
		return averageSalary;
	}
	public void setAverageSalary(BigDecimal averageSalary) {
		this.averageSalary = averageSalary;
	}
	public BigDecimal getMinSalary() {
		return minSalary;
	}
	public void setMinSalary(BigDecimal minSalary) {
		this.minSalary = minSalary;
	}
	public BigDecimal getMaxSalary() {
		return maxSalary;
	}
	public void setMaxSalary(BigDecimal maxSalary) {
		this.maxSalary = maxSalary;
	}
    
}