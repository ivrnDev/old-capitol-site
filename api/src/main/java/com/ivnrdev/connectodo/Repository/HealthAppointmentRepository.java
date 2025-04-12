package com.ivnrdev.connectodo.Repository;

import com.ivnrdev.connectodo.Domain.HealthAppointment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthAppointmentRepository extends CrudRepository<HealthAppointment, Long> {
}
