package com.ems.service;

import com.ems.dto.request.CreateEmployeeRequest;
import com.ems.dto.request.UpdateEmployeeRequest;
import com.ems.dto.response.EmployeeResponse;
import com.ems.dto.response.PagedResponse;
import com.ems.entity.Employee;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {

    EmployeeResponse create(CreateEmployeeRequest request);

    EmployeeResponse getById(Long id);

    EmployeeResponse getByCode(String employeeCode);

    PagedResponse<EmployeeResponse> getAll(Pageable pageable);

    PagedResponse<EmployeeResponse> search(String search, Long departmentId,
                                           Employee.Status status, Pageable pageable);

    PagedResponse<EmployeeResponse> getByDepartment(Long departmentId, Pageable pageable);

    PagedResponse<EmployeeResponse> getByManager(Long managerId, Pageable pageable);

    EmployeeResponse update(Long id, UpdateEmployeeRequest request);

    void delete(Long id);

    EmployeeResponse changeStatus(Long id, Employee.Status status);
}
