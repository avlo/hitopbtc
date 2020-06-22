package com.hitop.service.stub;

import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.Transaction;

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

import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.hitop.service.WalletService;

@Service
@ConditionalOnProperty(
    name = "spring.profiles.active", 
    havingValue = "dev")
public class StubWalletService implements WalletService {
  private final static Logger log = LoggerFactory.getLogger(StubWalletService.class);
  
  private StubReceivedService stubReceivedService;
  
  public StubWalletService()  { }

  @Override
  public void addCoinsReceivedEventListener(final WalletCoinsReceivedEventListener listener) {
  }
  
  @Override
  // TODO: Refactor Transaction to be non-bitcoin specific (replace w/ interface & currency-specific wrapper impl)
  public String getTxReceiveAddress(Transaction tx) {
    return "STUB ADDRESS";
  }

  @Override
  public String getFreshSendToAddress() {
    return "STUB WALLET";
  }
  
  @Override
  public String getBalance() {
    // TODO Auto-generated method stub
    return "STUB";
  }
  
@Override
  public String getMinTxFee() {
    // TODO Auto-generated method stub
    return "STUB";
  }
  
  @Override
  public boolean sendBalanceTo(String address) throws InsufficientMoneyException {
    // TODO Auto-generated method stub
    return true;
  }
}
