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

import javax.annotation.PostConstruct;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.SegwitAddress;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.SendRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.hitop.NetworkParameters;
import com.hitop.service.TransactionWrapper;
import com.hitop.service.WalletService;

@Service
@ConditionalOnProperty(
    name = "spring.profiles.active", 
    havingValue = "test")
public class BitcoinWalletService implements WalletService {
  private final static Logger log = LoggerFactory.getLogger(BitcoinWalletService.class);

  private final WalletAppKit walletAppKit;
  private final NetworkParameters parameters;
  private final BitcoinReceivedService bitcoinReceivedService;

  @Autowired
  public BitcoinWalletService(
      final NetworkParameters params,
      final WalletAppKit walletAppKit,
      final BitcoinReceivedService bitcoinReceivedService) throws Exception {
    this.parameters = params;
    this.walletAppKit = walletAppKit;
    this.bitcoinReceivedService = bitcoinReceivedService;
  }

  @PostConstruct
  private void postConstruct() {
    this.walletAppKit.wallet().addCoinsReceivedEventListener(bitcoinReceivedService);
  }

  @Override
  public String getTxReceiveAddress(final TransactionWrapper tx) {
    return tx.getTxReceiveAddress();
  }

  @Override
  public String getFreshSendToAddress() {
    // TODO: issue w/ Segwit, replace when fixed
    return LegacyAddress.fromKey(this.parameters.getNetworkParameters(), walletAppKit.wallet().freshReceiveKey()).toString();
  }
  
  @Override
  public Address getLegacySendToAddress(final String address) {
    return LegacyAddress.fromString(this.parameters.getNetworkParameters(), address);
  }
  
  // TODO: issue w/ Segwit, replace when fixed
  public Address getSegwitSendToAddress(final String address) {
    return SegwitAddress.fromString(this.parameters.getNetworkParameters(), address);
  }

  @Override
  public String getBalance() {
    return getCoinBalance().toFriendlyString();
  }

  @Override
  public Coin getCoinBalance() {
    walletAppKit.wallet().cleanup();
    return walletAppKit.wallet().getBalance();
  }
  
  @Override
  public boolean sendMoney(final SendRequest req) {
    try {
      walletAppKit.wallet().sendCoins(req);
      return true;
    } catch (InsufficientMoneyException e) {
      log.info("************************************************************************");
      log.info("************* SHOULD NOT OCCUR SINGE WE'RE CHECKING FOR THIS ***********");
      log.info("************************************************************************");
      log.info("************************************************************************");
      log.info("exception getMessage() {}", e.getMessage());
      log.info("************************************************************************");
      log.info("exception getLocalizedMessage() {}", e.getLocalizedMessage());
      log.info("************************************************************************");
      log.info("exception toFriendlyString() {}", e.missing.toFriendlyString());
      log.info("************************************************************************");
      return false;
    }
  }
}
