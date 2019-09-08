package com.hitop.repository;

import org.springframework.data.repository.CrudRepository;
import com.hitop.entity.HitopOrder;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface HitopOrderRepository extends CrudRepository<HitopOrder, Integer> {
}
