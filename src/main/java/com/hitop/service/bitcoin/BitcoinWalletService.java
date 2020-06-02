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

import java.io.File;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.utils.BriefLogFormatter;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.hitop.NetworkParameters;
import com.hitop.service.WalletFile;
import com.hitop.service.WalletService;

@Service
@ConditionalOnProperty(
    name = "spring.profiles.active", 
    havingValue = "test")
public class BitcoinWalletService implements WalletService {
  final Logger logger = LoggerFactory.getLogger(BitcoinWalletService.class);
  
  private final WalletAppKit kit;
  private final NetworkParameters params;
  
  @Autowired
  public BitcoinWalletService(final NetworkParameters params, final WalletFile walletFile) throws Exception {
    this.params = params;
    
    logger.info(walletFile.toString());
    
    System.out.println("***********");
    System.out.println("***********");
    System.out.println(params);
    System.out.println("-----------");
    System.out.println(walletFile.getFilePrefix());
    System.out.println("***********");
    System.out.println("***********");

    // log output more compact and easily read, especially when using the JDK log adapter.
    BriefLogFormatter.init();
    
    // Start up a basic app using a class that automates some boilerplate.
    kit = new WalletAppKit(params.getNetworkParameters(), new File("."), walletFile.getFilePrefix()) {
      @Override
      protected void onSetupCompleted() {
        // TODO 60: use unconfirmed for now for expediency
        kit.wallet().allowSpendingUnconfirmedTransactions();
        logger.info("walletAppKit setup complete.");
      }
    };

    // Download the block chain and wait until it's done.
    kit.startAsync();
    kit.awaitRunning();
  }

  @Override
  public void addCoinsReceivedEventListener(final WalletCoinsReceivedEventListener listener) throws Exception {
    kit.wallet().addCoinsReceivedEventListener(listener);
  }

  @Override
  public String getSendToAddress() {
    return LegacyAddress.fromKey(params.getNetworkParameters(), kit.wallet().currentReceiveKey()).toString();
  }
}
