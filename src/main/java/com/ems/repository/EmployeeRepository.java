package com.ems.repository;

import com.ems.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>,
        JpaSpecificationExecutor<Employee> {

    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByEmployeeCode(String employeeCode);

    boolean existsByEmail(String email);

    boolean existsByEmployeeCode(String employeeCode);

    Page<Employee> findByDepartmentId(Long departmentId, Pageable pageable);

    Page<Employee> findByStatus(Employee.Status status, Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE e.manager.id = :managerId")
    Page<Employee> findByManagerId(@Param("managerId") Long managerId, Pageable pageable);

    @Query("""
        SELECT e FROM Employee e
        WHERE (:search IS NULL OR LOWER(e.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(e.email) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', :search, '%')))
        AND (:departmentId IS NULL OR e.department.id = :departmentId)
        AND (:status IS NULL OR e.status = :status)
        """)
    Page<Employee> searchEmployees(
            @Param("search") String search,
            @Param("departmentId") Long departmentId,
            @Param("status") Employee.Status status,
            Pageable pageable
    );

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.department.id = :deptId AND e.status = 'ACTIVE'")
    long countActiveByDepartment(@Param("deptId") Long deptId);
}
