package com.ivnrdev.connectodo.Controller;

import com.ivnrdev.connectodo.DTO.Employee.EmployeeRequestDTO;
import com.ivnrdev.connectodo.DTO.Employee.EmployeeResponseDTO;
import com.ivnrdev.connectodo.Domain.Employee;
import com.ivnrdev.connectodo.Mapper.EmployeeMapper;
import com.ivnrdev.connectodo.Response.BaseResponse;
import com.ivnrdev.connectodo.Service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;
    private final EmployeeMapper employeeMapper;

    @PostMapping
    public ResponseEntity<BaseResponse<EmployeeResponseDTO>> saveEmployee(@RequestBody EmployeeRequestDTO employee) {
        Employee savedEmployee = employeeService.saveEmployee(employeeMapper.toEntity(employee));
        EmployeeResponseDTO employeeResponseDTO = employeeMapper.toRes(savedEmployee);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponse.success(employeeResponseDTO, "Employee saved successfully")
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<EmployeeResponseDTO>>> getAllEmployees() {
        List<EmployeeResponseDTO> employees = employeeService.getAllEmployees().stream().map(data -> employeeMapper.toRes(data)).collect(Collectors.toList());
        return ResponseEntity.ok(
                BaseResponse.success(employees, "Employees fetched successfully")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<EmployeeResponseDTO>> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        EmployeeResponseDTO employeeResponseDTO = employeeMapper.toRes(employee);
        return ResponseEntity.ok(
                BaseResponse.success(employeeResponseDTO, "Employee fetched successfully")
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponse<EmployeeResponseDTO>> updateEmployee(@PathVariable Long id, @RequestBody EmployeeRequestDTO employee) {
        Employee updatedEmployee = employeeService.updateEmployeeById(id, employeeMapper.toEntity(employee));
        EmployeeResponseDTO employeeResponseDTO = employeeMapper.toRes(updatedEmployee);
        return ResponseEntity.ok(
                BaseResponse.success(employeeResponseDTO, "Employee updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<EmployeeResponseDTO>> deleteEmployee(@PathVariable Long id) {
        Employee deletedEployee = employeeService.deleteEmployeeById(id);
        EmployeeResponseDTO employeeResponseDTO = employeeMapper.toRes(deletedEployee);
        return ResponseEntity.ok(
                BaseResponse.success(employeeResponseDTO, "Employee deleted successfully")
        );
    }
}
