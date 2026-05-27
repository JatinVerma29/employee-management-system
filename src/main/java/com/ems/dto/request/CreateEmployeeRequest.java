package com.ems.dto.request;

import com.ems.entity.Employee;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CreateEmployeeRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name cannot exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^[+]?[0-9]{7,15}$", message = "Invalid phone number")
    private String phone;

    private LocalDate dateOfBirth;

    private Employee.Gender gender;

    private String address;

    @NotBlank(message = "Designation is required")
    @Size(max = 100)
    private String designation;

    @NotNull(message = "Salary is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be greater than 0")
    private BigDecimal salary;

    @NotNull(message = "Hire date is required")
    private LocalDate hireDate;

    private Employee.EmploymentType employmentType = Employee.EmploymentType.FULL_TIME;

    private Long departmentId;

    private Long managerId;
}
