package com.hitop.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hitop.entity.PurchaseOrder;
import com.hitop.repository.OrderRepository;

@Service
public class OrderServiceImpl {
  Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
  
  private final OrderRepository orderRepository;
  
  @Autowired
  public OrderServiceImpl(final OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }
  
  public PurchaseOrder save(PurchaseOrder order) {
    PurchaseOrder savedOrder = orderRepository.save(order);
    logger.info(String.format("order %s saved to db.", savedOrder));
    return savedOrder;
  }
}

