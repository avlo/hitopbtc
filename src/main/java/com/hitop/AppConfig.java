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
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.script.Script;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.SPVBlockStore;
import org.bitcoinj.wallet.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    log.info("bitcoin wallet (app kit) file {}", walletFile.toString());
    
    log.info("***********");
    log.info("BITCOIN WALLET_APP_KIT");
    log.info("***********");
    log.info(params.toString());
    log.info("-----------");
    log.info(walletFile.getFilePrefix());
    log.info("***********");
    log.info("BITCOIN WALLET_APP_KIT");
    log.info("***********");
    
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
  @ConditionalOnExpression("${bitcoin.bean:false}")
  public org.bitcoinj.wallet.Wallet bitcoinWallet(
      final @Value("${bitcoin.wallet.xpub}") String pub,
      final BitcoinNetworkParameters params,
      final BWalletFile walletFile) throws BlockStoreException, InterruptedException {

    log.info("bitcoin wallet (no app kit) file {}", walletFile.toString());
    
    log.info("***********");
    log.info("BITCOIN WALLET (NO APP KIT)");
    log.info("***********");
    log.info(params.toString());
    log.info("-----------");
    log.info(walletFile.getFilePrefix());
    log.info("-----------");
    
    // log output more compact and easily read, especially when using the JDK log adapter.
    org.bitcoinj.utils.BriefLogFormatter.init();
    
    NetworkParameters networkParameters = params.getNetworkParameters();
    
    DeterministicKey keyChainSeed =  DeterministicKey.deserializeB58(null, pub, networkParameters);
    DeterministicKey key = HDKeyDerivation.deriveChildKey(keyChainSeed, new ChildNumber(0, false));
    Wallet wallet = Wallet.fromWatchingKey(networkParameters, key, Script.ScriptType.P2PKH);

    // Setting up the BlochChain, the BlocksStore and connecting to the network.
    SPVBlockStore chainStore = new SPVBlockStore(networkParameters, walletFile.getFile());
    BlockChain chain = new BlockChain(networkParameters, chainStore);
    PeerGroup peerGroup = new PeerGroup(networkParameters, chain);
    peerGroup.addPeerDiscovery(new DnsDiscovery(networkParameters));

    // Now we need to hook the wallet up to the blockchain and the peers. This registers event listeners that notify our wallet about new transactions.
    chain.addWallet(wallet);
    peerGroup.addWallet(wallet);

    DownloadProgressTracker bListener = new DownloadProgressTracker() {
        @Override
        public void doneDownload() {
          log.info("@@@@@@@@@@@@@@@@@@@@@");
          log.info("blockchain downloaded");
          log.info("@@@@@@@@@@@@@@@@@@@@@");
        }
    };

    // Now we re-download the blockchain. This replays the chain into the wallet. Once this is completed our wallet should know of all its transactions and print the correct balance.
    peerGroup.start();
    peerGroup.startBlockChainDownload(bListener);

    bListener.await();

    return wallet;
  }

  @Bean
  @ConditionalOnExpression("${litecoin.bean:false}")
  public org.litecoinj.kits.WalletAppKit litecoinWalletAppKit(final LitecoinNetworkParameters params, final LWalletFile walletFile) {

    log.info("litecoin wallet file {}", walletFile.toString());

    log.info("***********");
    log.info("LITECOIN");
    log.info("***********");
    log.info(params.toString());
    log.info("-----------");
    log.info(walletFile.getFilePrefix());
    log.info("***********");
    log.info("LITECOIN");
    log.info("***********");

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