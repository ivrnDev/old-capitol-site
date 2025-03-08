package com.ivnrdev.connectodo.Repository;

import com.ivnrdev.connectodo.Domain.Employee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends CrudRepository<Employee, Long> {
    Employee findByUsername(String username);

    Employee findByEmail(String email);
}
