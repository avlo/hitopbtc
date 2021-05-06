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

import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.kits.WalletAppKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import com.google.common.util.concurrent.ListenableFuture;
import com.hitop.service.TransactionWrapper;

@Component
@ConditionalOnProperty(
    name = "spring.profiles.active",
    havingValue = "test")
@ConditionalOnExpression("${bitcoin.bean:false}")
public class BitcoinTransactionWrapper implements TransactionWrapper {
  private final static Logger log = LoggerFactory.getLogger(BitcoinTransactionWrapper.class);

  private Transaction transaction;
  private final WalletAppKit bitcoinWalletAppKit;
  private final BitcoinNetworkParameters bitcoinNetworkParameters;

  @Autowired
  public BitcoinTransactionWrapper(
      final BitcoinNetworkParameters bitcoinNetworkParameters,
      final WalletAppKit bitcoinWalletAppKit) {
    this.bitcoinNetworkParameters = bitcoinNetworkParameters;
    this.bitcoinWalletAppKit = bitcoinWalletAppKit;
  }

  public void setTransaction(final Transaction transaction) {
    this.transaction = transaction;
  }

  public ListenableFuture<TransactionConfidence> getDepthFuture() {
    return transaction.getConfidence().getDepthFuture(1);
  }

  @Override
  public String getTxReceiveAddress() {
    // TODO: replace for/if with functional functionalIF / lambda
    for(TransactionOutput txo : transaction.getOutputs()){
      if (txo.isMine(bitcoinWalletAppKit.wallet())) {
        String walletTxo = txo.getScriptPubKey().getToAddress(bitcoinNetworkParameters.getNetworkParameters(), true).toString();
        log.info("wallet txo: {}", walletTxo);
        return walletTxo;
      }
    }
    log.error("TXO not found in our wallet");
    // TODO: needs exception handling/wrapper
    return "****** REPLACE THIS STRING WITH \"TXO NOT IN OUR WALLET\" EXCEPTION ************";
  }
}
