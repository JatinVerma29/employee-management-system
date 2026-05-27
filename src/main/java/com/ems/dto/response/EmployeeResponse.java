package com.ems.dto.response;

import com.ems.entity.Employee;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeResponse {

    private Long id;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private Employee.Gender gender;
    private String address;
    private String designation;
    private BigDecimal salary;
    private LocalDate hireDate;
    private Employee.EmploymentType employmentType;
    private Employee.Status status;
    private DepartmentSummary department;
    private ManagerSummary manager;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Builder
    public static class DepartmentSummary {
        private Long id;
        private String name;
        private String code;
    }

    @Getter
    @Builder
    public static class ManagerSummary {
        private Long id;
        private String fullName;
        private String designation;
    }
}
