package com.ivnrdev.connectodo.Repository;

import com.ivnrdev.connectodo.Domain.Cedula;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CedulaRepository extends CrudRepository<Cedula, Long> {
}
