package com.ivnrdev.connectodo.Repository;

import com.ivnrdev.connectodo.Domain.Event;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends CrudRepository<Event, Long> {
}
