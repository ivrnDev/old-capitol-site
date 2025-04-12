package com.ivnrdev.connectodo.Repository;

import com.ivnrdev.connectodo.Domain.Patient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends CrudRepository<Patient, Long> {
}
