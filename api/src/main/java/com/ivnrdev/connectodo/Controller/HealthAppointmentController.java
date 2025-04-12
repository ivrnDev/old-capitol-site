package com.ivnrdev.connectodo.Controller;

import com.ivnrdev.connectodo.Domain.HealthAppointment;
import com.ivnrdev.connectodo.Repository.HealthAppointmentRepository;
import com.ivnrdev.connectodo.Response.BaseResponse;
import com.ivnrdev.connectodo.Service.HealthAppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health_appointments")
@RequiredArgsConstructor
public class HealthAppointmentController {
    private final HealthAppointmentRepository healthAppointmentRepository;
    private final HealthAppointmentService healthAppointmentService;

    @PostMapping
    public ResponseEntity<BaseResponse<HealthAppointment>> saveHealthAppointment(@RequestBody HealthAppointment healthAppointment) {
        HealthAppointment savedHealthAppointment = healthAppointmentService.saveHealthAppointment(healthAppointment);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponse.success(savedHealthAppointment, "Health Appointment saved successfully")
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<HealthAppointment>>> getAllHealthAppointments() {
        List<HealthAppointment> healthAppointments = healthAppointmentService.getAllHealthAppointments();
        return ResponseEntity.ok(
                BaseResponse.success(healthAppointments, "HealthAppointments fetched successfully")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<HealthAppointment>> getHealthAppointmentById(@PathVariable Long id) {
        HealthAppointment healthAppointment = healthAppointmentService.getHealthAppointmentById(id);
        return ResponseEntity.ok(
                BaseResponse.success(healthAppointment, "HealthAppointment fetched successfully")
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponse<HealthAppointment>> updateHealthAppointment(@PathVariable Long id, @RequestBody HealthAppointment healthAppointment) {
        HealthAppointment updatedHealthAppointment = healthAppointmentService.updateHealthAppointmentById(id, healthAppointment);
        return ResponseEntity.ok(
                BaseResponse.success(updatedHealthAppointment, "HealthAppointment updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<HealthAppointment>> deleteHealthAppointment(@PathVariable Long id) {
        HealthAppointment deletedEployee = healthAppointmentService.deleteHealthAppointmentById(id);
        return ResponseEntity.ok(
                BaseResponse.success(deletedEployee, "HealthAppointment deleted successfully")
        );
    }
}
