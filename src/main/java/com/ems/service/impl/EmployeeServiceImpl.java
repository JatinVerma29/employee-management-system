package com.ems.service.impl;

import com.ems.dto.request.CreateEmployeeRequest;
import com.ems.dto.request.UpdateEmployeeRequest;
import com.ems.dto.response.EmployeeResponse;
import com.ems.dto.response.PagedResponse;
import com.ems.entity.Department;
import com.ems.entity.Employee;
import com.ems.exception.DuplicateResourceException;
import com.ems.exception.ResourceNotFoundException;
import com.ems.repository.DepartmentRepository;
import com.ems.repository.EmployeeRepository;
import com.ems.service.EmployeeService;
import com.ems.util.EmployeeCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeCodeGenerator codeGenerator;

    @Override
    @Transactional
    public EmployeeResponse create(CreateEmployeeRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Employee with email already exists: " + request.getEmail());
        }

        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));
        }

        Employee manager = null;
        if (request.getManagerId() != null) {
            manager = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee (manager)", "id", request.getManagerId()));
        }

        Employee employee = Employee.builder()
                .employeeCode(codeGenerator.generate())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .address(request.getAddress())
                .designation(request.getDesignation())
                .salary(request.getSalary())
                .hireDate(request.getHireDate())
                .employmentType(request.getEmploymentType() != null
                        ? request.getEmploymentType() : Employee.EmploymentType.FULL_TIME)
                .department(department)
                .manager(manager)
                .build();

        employee = employeeRepository.save(employee);
        log.info("Created employee: {} ({})", employee.getFullName(), employee.getEmployeeCode());
        return toResponse(employee);
    }

    @Override
    public EmployeeResponse getById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        return toResponse(employee);
    }

    @Override
    public EmployeeResponse getByCode(String employeeCode) {
        Employee employee = employeeRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "code", employeeCode));
        return toResponse(employee);
    }

    @Override
    public PagedResponse<EmployeeResponse> getAll(Pageable pageable) {
        Page<EmployeeResponse> page = employeeRepository.findAll(pageable)
                .map(this::toResponse);
        return PagedResponse.from(page);
    }

    @Override
    public PagedResponse<EmployeeResponse> search(String search, Long departmentId,
                                                   Employee.Status status, Pageable pageable) {
        Page<EmployeeResponse> page = employeeRepository
                .searchEmployees(search, departmentId, status, pageable)
                .map(this::toResponse);
        return PagedResponse.from(page);
    }

    @Override
    public PagedResponse<EmployeeResponse> getByDepartment(Long departmentId, Pageable pageable) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new ResourceNotFoundException("Department", "id", departmentId);
        }
        Page<EmployeeResponse> page = employeeRepository
                .findByDepartmentId(departmentId, pageable)
                .map(this::toResponse);
        return PagedResponse.from(page);
    }

    @Override
    public PagedResponse<EmployeeResponse> getByManager(Long managerId, Pageable pageable) {
        employeeRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee (manager)", "id", managerId));
        Page<EmployeeResponse> page = employeeRepository
                .findByManagerId(managerId, pageable)
                .map(this::toResponse);
        return PagedResponse.from(page);
    }

    @Override
    @Transactional
    public EmployeeResponse update(Long id, UpdateEmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        if (request.getEmail() != null && !request.getEmail().equals(employee.getEmail())) {
            if (employeeRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Email already in use: " + request.getEmail());
            }
            employee.setEmail(request.getEmail());
        }

        if (request.getFirstName() != null) employee.setFirstName(request.getFirstName());
        if (request.getLastName() != null) employee.setLastName(request.getLastName());
        if (request.getPhone() != null) employee.setPhone(request.getPhone());
        if (request.getDateOfBirth() != null) employee.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) employee.setGender(request.getGender());
        if (request.getAddress() != null) employee.setAddress(request.getAddress());
        if (request.getDesignation() != null) employee.setDesignation(request.getDesignation());
        if (request.getSalary() != null) employee.setSalary(request.getSalary());
        if (request.getEmploymentType() != null) employee.setEmploymentType(request.getEmploymentType());
        if (request.getStatus() != null) employee.setStatus(request.getStatus());

        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));
            employee.setDepartment(dept);
        }

        if (request.getManagerId() != null) {
            if (request.getManagerId().equals(id)) {
                throw new IllegalArgumentException("Employee cannot be their own manager");
            }
            Employee manager = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee (manager)", "id", request.getManagerId()));
            employee.setManager(manager);
        }

        employee = employeeRepository.save(employee);
        log.info("Updated employee: {}", employee.getEmployeeCode());
        return toResponse(employee);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        employeeRepository.delete(employee);
        log.info("Deleted employee: {}", employee.getEmployeeCode());
    }

    @Override
    @Transactional
    public EmployeeResponse changeStatus(Long id, Employee.Status status) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        employee.setStatus(status);
        employee = employeeRepository.save(employee);
        log.info("Changed status of employee {} to {}", employee.getEmployeeCode(), status);
        return toResponse(employee);
    }

    private EmployeeResponse toResponse(Employee e) {
        EmployeeResponse.DepartmentSummary deptSummary = null;
        if (e.getDepartment() != null) {
            deptSummary = EmployeeResponse.DepartmentSummary.builder()
                    .id(e.getDepartment().getId())
                    .name(e.getDepartment().getName())
                    .code(e.getDepartment().getCode())
                    .build();
        }

        EmployeeResponse.ManagerSummary managerSummary = null;
        if (e.getManager() != null) {
            managerSummary = EmployeeResponse.ManagerSummary.builder()
                    .id(e.getManager().getId())
                    .fullName(e.getManager().getFullName())
                    .designation(e.getManager().getDesignation())
                    .build();
        }

        return EmployeeResponse.builder()
                .id(e.getId())
                .employeeCode(e.getEmployeeCode())
                .firstName(e.getFirstName())
                .lastName(e.getLastName())
                .fullName(e.getFullName())
                .email(e.getEmail())
                .phone(e.getPhone())
                .dateOfBirth(e.getDateOfBirth())
                .gender(e.getGender())
                .address(e.getAddress())
                .designation(e.getDesignation())
                .salary(e.getSalary())
                .hireDate(e.getHireDate())
                .employmentType(e.getEmploymentType())
                .status(e.getStatus())
                .department(deptSummary)
                .manager(managerSummary)
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
