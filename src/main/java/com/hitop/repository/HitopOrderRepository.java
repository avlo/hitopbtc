package com.hitop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hitop.entity.HitopOrder;

public interface HitopOrderRepository extends JpaRepository<HitopOrder, Integer> {
}
