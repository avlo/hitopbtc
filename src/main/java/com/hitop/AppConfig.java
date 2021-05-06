package com.hitop;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import com.hitop.service.bitcoin.BWalletFile;
import com.hitop.service.bitcoin.BitcoinNetworkParameters;
import com.hitop.service.litecoin.LWalletFile;
import com.hitop.service.litecoin.LitecoinNetworkParameters;

@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig {
  private final static Logger log = LoggerFactory.getLogger(AppConfig.class);
  
  @Bean
  @ConditionalOnExpression("${bitcoin.bean:false}")
  public org.bitcoinj.kits.WalletAppKit bitcoinWalletAppKit(final BitcoinNetworkParameters params, final BWalletFile walletFile) {

    log.info("bitcoin wallet file {}", walletFile.toString());
    
    log.debug("***********");
    log.debug("BITCOIN");
    log.debug("***********");
    log.debug(params.toString());
    log.debug("-----------");
    log.debug(walletFile.getFilePrefix());
    log.debug("***********");
    log.debug("BITCOIN");
    log.debug("***********");
    
    // log output more compact and easily read, especially when using the JDK log adapter.
    org.bitcoinj.utils.BriefLogFormatter.init();

    org.bitcoinj.kits.WalletAppKit kit = new org.bitcoinj.kits.WalletAppKit(params.getNetworkParameters(), new File("."), walletFile.getFilePrefix()) {
      @Override
      protected void onSetupCompleted() {
        log.info("bitcoinWalletAppKit setup complete.");
      }
    };

    // Download the block chain and wait until it's done.
    kit.startAsync();
    kit.awaitRunning();
    
    return kit;
  }
  
  @Bean
  @ConditionalOnExpression("${litecoin.bean:false}")
  public org.litecoinj.kits.WalletAppKit litecoinWalletAppKit(final LitecoinNetworkParameters params, final LWalletFile walletFile) {

    log.info("litecoin wallet file {}", walletFile.toString());
    
    log.debug("***********");
    log.debug("LITECOIN");
    log.debug("***********");
    log.debug(params.toString());
    log.debug("-----------");
    log.debug(walletFile.getFilePrefix());
    log.debug("***********");
    log.debug("LITECOIN");
    log.debug("***********");
    
    // log output more compact and easily read, especially when using the JDK log adapter.
    org.litecoinj.utils.BriefLogFormatter.init();

    org.litecoinj.kits.WalletAppKit kit = new org.litecoinj.kits.WalletAppKit(params.getNetworkParameters(), new File("."), walletFile.getFilePrefix()) {
      @Override
      protected void onSetupCompleted() {
        log.info("litecoinWalletAppKit setup complete.");
      }
    };

    // Download the block chain and wait until it's done.
    kit.startAsync();
    kit.awaitRunning();
    
    return kit;
  }
}