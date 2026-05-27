package com.ems.controller;

import com.ems.dto.request.CreateEmployeeRequest;
import com.ems.dto.request.UpdateEmployeeRequest;
import com.ems.dto.response.ApiResponse;
import com.ems.dto.response.EmployeeResponse;
import com.ems.dto.response.PagedResponse;
import com.ems.entity.Employee;
import com.ems.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Tag(name = "Employees", description = "Employee CRUD and search operations")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @Operation(summary = "Create a new employee")
    public ResponseEntity<ApiResponse<EmployeeResponse>> create(
            @Valid @RequestBody CreateEmployeeRequest request) {
        EmployeeResponse response = employeeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Employee created successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.getById(id)));
    }

    @GetMapping("/code/{employeeCode}")
    @Operation(summary = "Get employee by employee code")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getByCode(@PathVariable String employeeCode) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.getByCode(employeeCode)));
    }

    @GetMapping
    @Operation(summary = "Get all employees (paginated)")
    public ResponseEntity<ApiResponse<PagedResponse<EmployeeResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(ApiResponse.success(employeeService.getAll(pageable)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search employees with filters")
    public ResponseEntity<ApiResponse<PagedResponse<EmployeeResponse>>> search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Employee.Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        var pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
        return ResponseEntity.ok(
                ApiResponse.success(employeeService.search(query, departmentId, status, pageable)));
    }

    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Get employees by department")
    public ResponseEntity<ApiResponse<PagedResponse<EmployeeResponse>>> getByDepartment(
            @PathVariable Long departmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        var pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(
                ApiResponse.success(employeeService.getByDepartment(departmentId, pageable)));
    }

    @GetMapping("/{managerId}/direct-reports")
    @Operation(summary = "Get direct reports of a manager")
    public ResponseEntity<ApiResponse<PagedResponse<EmployeeResponse>>> getDirectReports(
            @PathVariable Long managerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        var pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(
                ApiResponse.success(employeeService.getByManager(managerId, pageable)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @Operation(summary = "Update employee details")
    public ResponseEntity<ApiResponse<EmployeeResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEmployeeRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Employee updated successfully", employeeService.update(id, request)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @Operation(summary = "Change employee status (ACTIVE, INACTIVE, ON_LEAVE, TERMINATED)")
    public ResponseEntity<ApiResponse<EmployeeResponse>> changeStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        Employee.Status status = Employee.Status.valueOf(body.get("status").toUpperCase());
        return ResponseEntity.ok(
                ApiResponse.success("Status updated", employeeService.changeStatus(id, status)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete an employee (Admin only)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Employee deleted", null));
    }
}
