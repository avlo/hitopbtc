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
import java.io.IOException;
import org.bitcoinj.wallet.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import com.hitop.service.BWalletFile;

@Component
@ConditionalOnProperty(
    name = "spring.profiles.active", 
    havingValue = "test")
@ConditionalOnExpression("${bitcoin.bean:false}")
public class BitcoinWalletFile implements BWalletFile {
  private final static Logger log = LoggerFactory.getLogger(BitcoinWalletFile.class);

  private final File file;

  public BitcoinWalletFile(final @Value("${bitcoin.wallet.filename.prefix}") String filePrefix) {
    this.file = new File(filePrefix);
    log.info("wallet filename: {}", filePrefix);
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
