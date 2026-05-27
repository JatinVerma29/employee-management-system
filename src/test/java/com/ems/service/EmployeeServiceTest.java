package com.ems.service;

import com.ems.dto.request.CreateEmployeeRequest;
import com.ems.dto.response.EmployeeResponse;
import com.ems.entity.Department;
import com.ems.entity.Employee;
import com.ems.exception.DuplicateResourceException;
import com.ems.exception.ResourceNotFoundException;
import com.ems.repository.DepartmentRepository;
import com.ems.repository.EmployeeRepository;
import com.ems.service.impl.EmployeeServiceImpl;
import com.ems.util.EmployeeCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeService Unit Tests")
class EmployeeServiceTest {

    @Mock private EmployeeRepository employeeRepository;
    @Mock private DepartmentRepository departmentRepository;
    @Mock private EmployeeCodeGenerator codeGenerator;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private CreateEmployeeRequest validRequest;
    private Employee savedEmployee;
    private Department department;

    @BeforeEach
    void setUp() {
        department = Department.builder()
                .id(1L).name("Engineering").code("ENG").isActive(true).build();

        validRequest = new CreateEmployeeRequest();
        validRequest.setFirstName("John");
        validRequest.setLastName("Doe");
        validRequest.setEmail("john.doe@test.com");
        validRequest.setDesignation("Software Engineer");
        validRequest.setSalary(BigDecimal.valueOf(75000));
        validRequest.setHireDate(LocalDate.now());
        validRequest.setDepartmentId(1L);

        savedEmployee = Employee.builder()
                .id(1L)
                .employeeCode("EMP-2024-1001")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .designation("Software Engineer")
                .salary(BigDecimal.valueOf(75000))
                .hireDate(LocalDate.now())
                .status(Employee.Status.ACTIVE)
                .employmentType(Employee.EmploymentType.FULL_TIME)
                .department(department)
                .build();
    }

    @Test
    @DisplayName("Should create employee successfully")
    void shouldCreateEmployeeSuccessfully() {
        given(employeeRepository.existsByEmail(anyString())).willReturn(false);
        given(departmentRepository.findById(1L)).willReturn(Optional.of(department));
        given(codeGenerator.generate()).willReturn("EMP-2024-1001");
        given(employeeRepository.save(any(Employee.class))).willReturn(savedEmployee);

        EmployeeResponse result = employeeService.create(validRequest);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john.doe@test.com");
        assertThat(result.getFullName()).isEqualTo("John Doe");
        assertThat(result.getEmployeeCode()).isEqualTo("EMP-2024-1001");
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException for duplicate email")
    void shouldThrowOnDuplicateEmail() {
        given(employeeRepository.existsByEmail("john.doe@test.com")).willReturn(true);

        assertThatThrownBy(() -> employeeService.create(validRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("john.doe@test.com");

        verify(employeeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when department not found")
    void shouldThrowWhenDepartmentNotFound() {
        given(employeeRepository.existsByEmail(anyString())).willReturn(false);
        given(departmentRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.create(validRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Department");
    }

    @Test
    @DisplayName("Should find employee by ID")
    void shouldFindEmployeeById() {
        given(employeeRepository.findById(1L)).willReturn(Optional.of(savedEmployee));

        EmployeeResponse result = employeeService.getById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when employee not found")
    void shouldThrowWhenEmployeeNotFound() {
        given(employeeRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Employee");
    }

    @Test
    @DisplayName("Should delete employee successfully")
    void shouldDeleteEmployee() {
        given(employeeRepository.findById(1L)).willReturn(Optional.of(savedEmployee));

        employeeService.delete(1L);

        verify(employeeRepository).delete(savedEmployee);
    }

    @Test
    @DisplayName("Should change employee status")
    void shouldChangeEmployeeStatus() {
        given(employeeRepository.findById(1L)).willReturn(Optional.of(savedEmployee));
        given(employeeRepository.save(any(Employee.class))).willReturn(savedEmployee);

        EmployeeResponse result = employeeService.changeStatus(1L, Employee.Status.ON_LEAVE);

        verify(employeeRepository).save(any(Employee.class));
    }
}
