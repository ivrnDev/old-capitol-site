package com.ivnrdev.connectodo.Repository;

import com.ivnrdev.connectodo.Domain.EventItems;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventItemsRepository extends CrudRepository<EventItems, Long> {
}
