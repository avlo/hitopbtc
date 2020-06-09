package com.hitop.service;

/*
 *  Copyright 2020 Nick Avlonitis
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */    

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hitop.entity.PurchaseOrder;
import com.hitop.repository.OrderRepository;

@Service
public class OrderServiceImpl {
  Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
  
  private final OrderRepository orderRepository;
  
  @Autowired
  public OrderServiceImpl(final OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }
  
  public PurchaseOrder save(final PurchaseOrder order) {
    PurchaseOrder savedOrder = orderRepository.save(order);
    log.info("order {} saved to db.", savedOrder);
    return savedOrder;
  }
  
  public PurchaseOrder get(final String btcPublicKey) {
    PurchaseOrder returnOrder = orderRepository.getOne(Integer.valueOf(btcPublicKey));
    log.info("order {} retrieved from db.", returnOrder);
    return returnOrder;
  }
}