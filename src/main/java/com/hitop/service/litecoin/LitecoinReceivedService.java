package com.hitop.service.litecoin;

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

import java.io.IOException;
import org.litecoinj.core.Coin;
import org.litecoinj.core.Transaction;
import org.litecoinj.wallet.Wallet;
import org.litecoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.hitop.controller.ReceiptListener;

@Service
@ConditionalOnProperty(
    name = "spring.profiles.active", 
    havingValue = "test")
public class LitecoinReceivedService implements WalletCoinsReceivedEventListener {
  private final static Logger log = LoggerFactory.getLogger(LitecoinReceivedService.class);

  private final LitecoinWalletFile litecoinWalletFile;
  private final LitecoinFuturesCallback litecoinFuturesCallback;
  private final LitecoinTransactionWrapper litecoinTransactionWrapper;
  
  // TODO: below receiptListener is field injected (instead of constructor injected) because
  //       docker complains about circular dependency when using constructor injection
  @Autowired
  private ReceiptListener receiptListener;
  
  
  @Autowired
  public LitecoinReceivedService(
      final LitecoinWalletFile litecoinWalletFile,
      final LitecoinFuturesCallback litecoinFuturesCallback,
      final LitecoinTransactionWrapper litecoinTransactionWrapper) {
    this.litecoinWalletFile = litecoinWalletFile;
    this.litecoinFuturesCallback = litecoinFuturesCallback;
    this.litecoinTransactionWrapper = litecoinTransactionWrapper;
  }

  /**
   * litecoin specific listenener.  when this method is called by litecoin network,
   * 1) take incoming parameters and construct TransactionWrapper object
   * 2) pass TransactionWrapper object to CompositionCoinReceived Service
  **/
  @Override
  public void onCoinsReceived(
      final Wallet wallet, 
      final Transaction tx, 
      final Coin prevBalance, 
      final Coin newBalance) {

    try {
      litecoinWalletFile.saveToFile(wallet);
    } catch (IOException e) {
      e.printStackTrace();
      log.info("{} save FAILED.", litecoinWalletFile.getFilePrefix());

    }

    log.info("Received tx for {} : {}", tx.getValueSentToMe(wallet).toFriendlyString(), tx);
    log.info("new value: {}", wallet.getBalance().getValue());
    log.info("---");
    log.info("coin prev balance : {}", prevBalance.toFriendlyString());
    log.info("coin new balance : {}", newBalance.toFriendlyString());

    litecoinTransactionWrapper.setTransaction(tx);
    litecoinFuturesCallback.addCallback(litecoinTransactionWrapper);
    
    // TODO: an outside payment to this address can cause this to fire, even though there's no UI listening for it
    receiptListener.displayReceiptSse(litecoinTransactionWrapper);
  }
}
