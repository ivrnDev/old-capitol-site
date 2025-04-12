package com.ivnrdev.connectodo.Controller;

import com.ivnrdev.connectodo.Domain.Patient;
import com.ivnrdev.connectodo.Response.BaseResponse;
import com.ivnrdev.connectodo.Service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<BaseResponse<Patient>> savePatient(@RequestBody Patient patient) {
        Patient savedPatient = patientService.savePatient(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponse.success(savedPatient, "Health Appointment saved successfully")
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<Patient>>> getAllPatients() {
        List<Patient> patients = patientService.getAllPatients();
        return ResponseEntity.ok(
                BaseResponse.success(patients, "Patients fetched successfully")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<Patient>> getPatientById(@PathVariable Long id) {
        Patient patient = patientService.getPatientById(id);
        return ResponseEntity.ok(
                BaseResponse.success(patient, "Patient fetched successfully")
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponse<Patient>> updatePatient(@PathVariable Long id, @RequestBody Patient patient) {
        Patient updatedPatient = patientService.updatePatientById(id, patient);
        return ResponseEntity.ok(
                BaseResponse.success(updatedPatient, "Patient updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Patient>> deletePatient(@PathVariable Long id) {
        Patient deletedEployee = patientService.deletePatientById(id);
        return ResponseEntity.ok(
                BaseResponse.success(deletedEployee, "Patient deleted successfully")
        );
    }
}
