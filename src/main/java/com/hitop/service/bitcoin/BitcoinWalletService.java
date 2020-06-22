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
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.SegwitAddress;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.utils.BriefLogFormatter;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;
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
  private final static Logger log = LoggerFactory.getLogger(BitcoinWalletService.class);

  private final NetworkParameters parameters;
  private final BitcoinRateService bitcoinRateService;
  private final WalletAppKit kit;
  private final Double minXferAmt;

  @Autowired
  public BitcoinWalletService(final BitcoinRateService bitcoinRateService,
      final @Value("${min.transfer.amount}") Double minXferAmt,
      final NetworkParameters params, 
      final WalletFile walletFile) throws Exception {
    this.parameters = params;
    this.bitcoinRateService = bitcoinRateService;
    this.minXferAmt = minXferAmt;

    log.info(walletFile.toString());

    log.debug("***********");
    log.debug("***********");
    log.debug(params.toString());
    log.debug("-----------");
    log.debug(walletFile.getFilePrefix());
    log.debug("***********");
    log.debug("***********");

    // log output more compact and easily read, especially when using the JDK log adapter.
    BriefLogFormatter.init();

    // Start up a basic app using a class that automates some boilerplate.
    kit = new WalletAppKit(params.getNetworkParameters(), new File("."), walletFile.getFilePrefix()) {
      @Override
      protected void onSetupCompleted() {
        Context.propagate(new Context(parameters.getNetworkParameters()));
        // TODO 60: use unconfirmed for now for expediency
        kit.wallet().allowSpendingUnconfirmedTransactions();
        log.info("walletAppKit setup complete.");
      }
    };

    // Download the block chain and wait until it's done.
    kit.startAsync();
    kit.awaitRunning();
  }

  public String getTxReceiveAddress(final Transaction tx) {
    // TODO: replace for/if with functional functionalIF / lambda
    for(TransactionOutput txo : tx.getOutputs()){
      if (txo.isMine(kit.wallet())) {
        String walletTxo = txo.getScriptPubKey().getToAddress(this.parameters.getNetworkParameters(), true).toString();
        log.info("wallet txo: {}", walletTxo);
        return walletTxo;
      }
    }
    log.error("TXO not found in our wallet");
    // TODO: needs exception handling/wrapper
    return "****** REPLACE THIS STRING WITH \"TXO NOT IN OUR WALLET\" EXCEPTION ************";
  }

  @Override
  public void addCoinsReceivedEventListener(final WalletCoinsReceivedEventListener listener) {
    kit.wallet().addCoinsReceivedEventListener(listener);
  }

  @Override
  public String getFreshSendToAddress() {
    // TODO: issue w/ Segwit, replace when fixed
    return LegacyAddress.fromKey(this.parameters.getNetworkParameters(), kit.wallet().freshReceiveKey()).toString();
  }

  @Override
  public String getBalance() {
    return getCoinBalance().toFriendlyString();
  }

  @Override
  public String getMinTxFee() {
    return getCoinMinTxFee().toFriendlyString();
  }

  private Coin getCoinBalance() {
    return kit.wallet().getBalance(Wallet.BalanceType.AVAILABLE_SPENDABLE);
  }

  private Coin getCoinMinTxFee() {
    return Transaction.REFERENCE_DEFAULT_MIN_TX_FEE;
  }
  
  private Coin convertBtcToSatoshis(Double btc) {
    return Coin.valueOf((long)Math.floor(btc * 100000000));
  }

  private Address getLegacySendToAddress(final String address) {
    return LegacyAddress.fromString(this.parameters.getNetworkParameters(), address);
  }
  
  public Double getMinWithdrawlInBtc(Double dollarAmount) {
    Double amount = dollarAmount / this.bitcoinRateService.getBtcRate();
    log.info("min btc {}", amount);
    return amount;
  }

  // TODO: issue w/ Segwit, replace when fixed
  private Address getSegwitSendToAddress(final String address) {
    return SegwitAddress.fromString(this.parameters.getNetworkParameters(), address);
  }

  
  private boolean confirmMinimumWalletAmount(final Coin spendable) {
    return spendable.isLessThan(convertBtcToSatoshis(getMinWithdrawlInBtc(this.minXferAmt))) ? false : true;
  }
  
  public boolean sendBalanceTo(final String addressStr) throws InsufficientMoneyException {
    Context.propagate(new Context(this.parameters.getNetworkParameters()));
    final Coin walletBalanceSpendable = getCoinBalance();
    final Coin minTxFee = getCoinMinTxFee();

    Coin spendable = walletBalanceSpendable.minus(minTxFee);
    
    log.info("wallet spendable {}", walletBalanceSpendable.toFriendlyString());
    log.info("min fee {}", minTxFee.toFriendlyString());
    log.info("spendable - fee: {}", spendable.toFriendlyString());
    
    if (!confirmMinimumWalletAmount(spendable)) {
      log.info("insufficient funds: {}", spendable.toFriendlyString());
      return false;
    }
    
    SendRequest req = SendRequest.to(getLegacySendToAddress(addressStr), spendable);
    req.feePerKb = minTxFee;
    
    log.info("send to address {}", addressStr);
    log.info("fee {}", req.feePerKb.toFriendlyString());

    kit.wallet().sendCoins(req);
    return true;
  }
}
