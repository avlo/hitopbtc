package com.hitop.service.bitcoin;

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
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.hitop.controller.ReceiptListener;

@Service
@ConditionalOnProperty(
    name = "spring.profiles.active", 
    havingValue = "test")
@ConditionalOnExpression("${bitcoin.bean:false}")
public class BitcoinReceivedService implements WalletCoinsReceivedEventListener {
  private final static Logger log = LoggerFactory.getLogger(BitcoinReceivedService.class);

  private final BitcoinWalletFile bitcoinWalletFile;
  private final BitcoinFuturesCallback bitcoinFuturesCallback;
  private final BitcoinTransactionWrapper bitcoinTransactionWrapper;
  
  // TODO: below receiptListener is field injected (instead of constructor injected) because
  //       docker complains about circular dependency when using constructor injection
  @Autowired
  private ReceiptListener receiptListener;

  @Autowired
  public BitcoinReceivedService(
      final BitcoinWalletFile bitcoinWalletFile,
      final BitcoinFuturesCallback bitcoinFuturesCallback,
      final BitcoinTransactionWrapper bitcoinTransactionWrapper) {
    this.bitcoinWalletFile = bitcoinWalletFile;
    this.bitcoinFuturesCallback = bitcoinFuturesCallback;
    this.bitcoinTransactionWrapper = bitcoinTransactionWrapper;
  }

  @Override
  public void onCoinsReceived(
      final Wallet wallet, 
      final Transaction tx, 
      final Coin prevBalance, 
      final Coin newBalance) {

    try {
      bitcoinWalletFile.saveToFile(wallet);
    } catch (IOException e) {
      e.printStackTrace();
      log.info("{} save FAILED.", bitcoinWalletFile.getFilePrefix());
    }

    log.info("Received tx for {} : {}", tx.getValueSentToMe(wallet).toFriendlyString(), tx);
    log.info("new value: {}", wallet.getBalance().getValue());
    log.info("---");
    log.info("coin prev balance : {}", prevBalance.toFriendlyString());
    log.info("coin new balance : {}", newBalance.toFriendlyString());

    bitcoinTransactionWrapper.setTransaction(tx);
    bitcoinFuturesCallback.addCallback(bitcoinTransactionWrapper);
    
    // TODO: an outside payment to this address can cause this to fire, even though there's no UI listening for it
    receiptListener.displayReceiptSse(bitcoinTransactionWrapper);
  }
}
