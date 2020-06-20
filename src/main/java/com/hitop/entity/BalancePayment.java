package com.hitop.entity;

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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.springframework.stereotype.Component;
import lombok.Data;

@Data
@Entity
@Component
public class BalancePayment {
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Long id;
  
  /**
   * "from" represents the current wallet parent HD address.
   * it exists because any subsequent app deployments will have 
   * a new parent HD address pointing to same db, and we'd like to keep 
   * track of all those parent HD addresses.
   * 
   * "from" is also also a way to check wallet balances on the
   * blockchain, such that wallet owner can then use related private key 
   * to move funds into an external wallet if desired
   */
  private String fromAddress;
  private String toAddress;
//  private Double startingBalance;
//  private Double endingBalance;
}
