package com.hitop.service.iota;

import java.util.Date;
import java.util.concurrent.ExecutionException;

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

import javax.annotation.PostConstruct;
import org.iota.jota.account.deposits.ConditionalDepositAddress;
import org.iota.jota.account.deposits.methods.DepositFactory;
import org.iota.jota.account.deposits.methods.MagnetMethod;
import org.iota.jota.account.errors.AccountError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.hitop.service.TransactionWrapper;
import com.hitop.service.WalletService;

@Service
@ConditionalOnProperty(
    name = "spring.profiles.active", 
    havingValue = "test")
public class IotaWalletService implements WalletService {
  private final static Logger log = LoggerFactory.getLogger(IotaWalletService.class);

  private final IotaWalletFile iotaWalletFile;

  @Autowired
  public IotaWalletService(final IotaWalletFile iotaWalletFile) throws Exception {
    this.iotaWalletFile = iotaWalletFile;
  }

  @PostConstruct
  private void postConstruct() {
//    this.walletAppKit.wallet().addCoinsReceivedEventListener(iotaReceivedService);
  }

  @Override
  public String getTxReceiveAddress(final TransactionWrapper tx) {
    return tx.getTxReceiveAddress();
  }

  @Override
  public String getFreshSendToAddress() {
    // Define the same time tomorrow
    Date timeoutAt = new Date(System.currentTimeMillis() + 24000 * 60 * 60);
    // Generate the CDA
    ConditionalDepositAddress cda;
    
    // TODO: first pass happy path, exceptions needs fixing
    try {
      cda = iotaWalletFile.getAccount().newDepositAddress(timeoutAt, true, 0).get();
      return (String) DepositFactory.get().build(cda, MagnetMethod.class);
    } catch (AccountError e) {
      e.printStackTrace();
      return "AccountError exception when calling IotaWalletService.getFreshSendToAddress()";
    } catch (InterruptedException e) {
      e.printStackTrace();
      return "InterruptedException when generating IotaWalletService.getFreshSendToAddress()";
    } catch (ExecutionException e) {
      e.printStackTrace();
      return "ExecutionException when generating IotaWalletService.getFreshSendToAddress()";
    } 
  }
}
