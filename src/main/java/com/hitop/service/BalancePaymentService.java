package com.hitop.service;

import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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

import org.springframework.stereotype.Service;
import com.hitop.entity.BalancePayment;
import com.hitop.repository.BalancePaymentRepository;

@Service
public class BalancePaymentService {
  Logger log = LoggerFactory.getLogger(BalancePaymentService.class);
  
  private final BalancePaymentRepository balancePaymentRepository;

  @Autowired
  public BalancePaymentService(final BalancePaymentRepository balancePaymentRepository) {
    this.balancePaymentRepository = balancePaymentRepository;
  }
  
  public BalancePayment save(final BalancePayment order) {
    BalancePayment savedOrder = balancePaymentRepository.save(order);
    log.info("order {} saved to db.", savedOrder);
    return savedOrder;
  }
  
//  public Collection<BalancePayment> findByFromAddress(final String fromAddress) {
//    Collection<BalancePayment> balancePayments = balancePaymentRepository.findByFrom(fromAddress);
//    log.info("balance payments {} retrieved from db.", balancePayments);
//    return balancePayments;
//  }
//  
//  public Collection<BalancePayment> findByToAddress(final String toAddress) {
//    Collection<BalancePayment> balancePayments = balancePaymentRepository.findByTo(toAddress);
//    log.info("balance payments {} retrieved from db.", balancePayments);
//    return balancePayments;
//  }
  
}
