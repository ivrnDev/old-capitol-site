package com.ivnrdev.connectodo.Repository;

import com.ivnrdev.connectodo.Domain.Certificate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateRepository extends CrudRepository<Certificate, Long> {
}
