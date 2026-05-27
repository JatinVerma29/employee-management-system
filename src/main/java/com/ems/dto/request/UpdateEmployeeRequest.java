package com.ems.dto.request;

import com.ems.entity.Employee;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class UpdateEmployeeRequest {

    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^[+]?[0-9]{7,15}$", message = "Invalid phone number")
    private String phone;

    private LocalDate dateOfBirth;

    private Employee.Gender gender;

    private String address;

    @Size(max = 100)
    private String designation;

    @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be greater than 0")
    private BigDecimal salary;

    private Employee.EmploymentType employmentType;

    private Employee.Status status;

    private Long departmentId;

    private Long managerId;
}
