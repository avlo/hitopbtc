package com.hitop.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hitop.entity.HitopOrder;
import com.hitop.repository.HitopOrderRepository;

@Service
public class OrderServiceImpl {
  Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
  
  private final HitopOrderRepository hitopOrderRepository;
  
  @Autowired
  public OrderServiceImpl(final HitopOrderRepository hitopOrderRepository) {
    this.hitopOrderRepository = hitopOrderRepository;
  }
  
  public HitopOrder save(HitopOrder hitopOrder) {
//    order.setStatus(status);
    HitopOrder savedOrder = hitopOrderRepository.save(hitopOrder);
    logger.info(String.format("order %s saved to db.", savedOrder));
    return savedOrder;
  }
}

