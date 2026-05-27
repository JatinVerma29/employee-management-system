package com.ems.service.impl;

import com.ems.dto.request.DepartmentRequest;
import com.ems.dto.response.DepartmentResponse;
import com.ems.dto.response.PagedResponse;
import com.ems.entity.Department;
import com.ems.exception.DuplicateResourceException;
import com.ems.exception.ResourceNotFoundException;
import com.ems.repository.DepartmentRepository;
import com.ems.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {
        if (departmentRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Department already exists: " + request.getName());
        }
        if (departmentRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Department code already in use: " + request.getCode());
        }

        Department dept = Department.builder()
                .name(request.getName())
                .code(request.getCode().toUpperCase())
                .description(request.getDescription())
                .build();

        dept = departmentRepository.save(dept);
        log.info("Created department: {} ({})", dept.getName(), dept.getCode());
        return toResponse(dept);
    }

    public DepartmentResponse getById(Long id) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
        return toResponse(dept);
    }

    public List<DepartmentResponse> getAll() {
        return departmentRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<DepartmentResponse> getActive() {
        return departmentRepository.findByIsActiveTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PagedResponse<DepartmentResponse> getAllPaged(Pageable pageable) {
        Page<DepartmentResponse> page = departmentRepository.findAll(pageable)
                .map(this::toResponse);
        return PagedResponse.from(page);
    }

    @Transactional
    public DepartmentResponse update(Long id, DepartmentRequest request) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));

        if (!dept.getName().equals(request.getName()) && departmentRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Department name already in use: " + request.getName());
        }

        dept.setName(request.getName());
        dept.setCode(request.getCode().toUpperCase());
        dept.setDescription(request.getDescription());

        dept = departmentRepository.save(dept);
        return toResponse(dept);
    }

    @Transactional
    public void deactivate(Long id) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
        dept.setIsActive(false);
        departmentRepository.save(dept);
        log.info("Deactivated department: {}", dept.getCode());
    }

    @Transactional
    public void delete(Long id) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
        long count = employeeRepository.countActiveByDepartment(id);
        if (count > 0) {
            throw new IllegalArgumentException(
                "Cannot delete department with " + count + " active employees. Reassign them first.");
        }
        departmentRepository.delete(dept);
    }

    private DepartmentResponse toResponse(Department d) {
        long count = employeeRepository.countActiveByDepartment(d.getId());
        return DepartmentResponse.builder()
                .id(d.getId())
                .name(d.getName())
                .code(d.getCode())
                .description(d.getDescription())
                .isActive(d.getIsActive())
                .activeEmployeeCount(count)
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}
