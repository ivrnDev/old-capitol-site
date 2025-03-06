package com.ivnrdev.connectodo.Repository;

import com.ivnrdev.connectodo.Domain.Resident;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResidentRepository extends CrudRepository<Resident, Long> {
}
