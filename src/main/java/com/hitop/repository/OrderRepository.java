package com.hitop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hitop.entity.PurchaseOrder;

public interface OrderRepository extends JpaRepository<PurchaseOrder, Integer> {
}
