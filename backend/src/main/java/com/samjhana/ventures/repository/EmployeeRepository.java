package com.samjhana.ventures.repository;

import com.samjhana.ventures.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByBusinessId(Long businessId);

    List<Employee> findByBusinessIdAndStatus(Long businessId, Employee.EmploymentStatus status);

    List<Employee> findByStatus(Employee.EmploymentStatus status);
}
