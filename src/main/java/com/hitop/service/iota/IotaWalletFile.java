package com.hitop.service.iota;

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
import java.io.IOException;
import org.bitcoinj.wallet.Wallet;
import org.iota.jota.IotaAccount;
import org.iota.jota.account.AccountStore;
import org.iota.jota.account.store.AccountFileStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import com.hitop.service.WalletFile;

@Component
@ConditionalOnProperty(
    name = "spring.profiles.active", 
    havingValue = "test")
public class IotaWalletFile implements WalletFile {
  private final static Logger log = LoggerFactory.getLogger(IotaWalletFile.class);

  private final File file;
  private final IotaNetworkParameters iotaNetworkParameters;
  private final IotaAccount account;
  private final IotaReceivedService iotaReceivedService;
  
  @Autowired
  public IotaWalletFile(
      final @Value("${iota.wallet.filename}") String fileName,
      final IotaNetworkParameters iotaNetworkParameters,
      final IotaReceivedService iotaReceivedService) {
    this.file = new File(fileName);
    this.iotaNetworkParameters = iotaNetworkParameters;
    this.iotaReceivedService = iotaReceivedService;
    AccountStore store = new AccountFileStore(this.file);

    log.info("IOTA wallet filename: {}", this.file);
    
    String mySeed = "PUETTSEITFEVEWCTBTSIZM9NKRGJEIMXTULBACGFRQK9IMGICLBKW9TTEVSDQMGWKBXPVCBMMCXWMNPDX";
    
    // Create an account, using your seed
    account = new IotaAccount.Builder(mySeed)
        // Connect to a node
        .api(this.iotaNetworkParameters.getIotaAPI())
        // Connect to the database
        .store(store)
        // Set the minimum weight magnitude for the Devnet (default is 14)
        .mwm(9)
        // Set a security level for CDAs (default is 3)
        .securityLevel(2)
        .plugin(this.iotaReceivedService)
        .build();

    // Start the account and any plugins
    account.start();
  }
  
  public IotaAccount getAccount() {
    return account;
  }

  public void saveToFile(final Wallet wallet) throws IOException {
    // Runs in the dedicated "user thread" (see bitcoinj docs for more info on this).
    // The transaction "tx" can either be pending, or included into a block (we didn't see the broadcast).
    log.info("saving file {}...", file.toString());
    wallet.saveToFile(file);
    log.info("{} saved.", file.toString());
  }

  public String getFilePrefix() {
    return this.file.getName();
  }
}
