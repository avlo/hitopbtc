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

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.wallet.SendRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.hitop.NetworkParameters;
import com.hitop.entity.BalancePayment;
import com.hitop.repository.BalancePaymentRepository;
import com.hitop.service.BalanceTransferService;
import com.hitop.service.WalletService;

@Service
public class BitcoinBalanceTransferService implements BalanceTransferService {
  Logger log = LoggerFactory.getLogger(BitcoinBalanceTransferService.class);
  
  private final BalancePaymentRepository balancePaymentRepository;
  private final NetworkParameters parameters;
  private final WalletService walletService;
  private final BitcoinRateService bitcoinRateService;
  private final Double minXferAmt;

  @Autowired
  public BitcoinBalanceTransferService(
      final @Value("${min.transfer.amount}") Double minXferAmt,
      final BalancePaymentRepository balancePaymentRepository,
      final NetworkParameters params, 
      final WalletService walletService, 
      final BitcoinRateService bitcoinRateService) {
    this.minXferAmt = minXferAmt;
    this.parameters = params;
    this.walletService = walletService;
    this.bitcoinRateService = bitcoinRateService;
    this.balancePaymentRepository = balancePaymentRepository;
  }
  
  @Override
  public String getMinTxFee() {
    return getCoinMinTxFee().toFriendlyString();
  }
  
  @Override
  public boolean sendBalanceTo(final String addressStr) throws InsufficientMoneyException {
    final Coin walletBalanceSpendable = walletService.getCoinBalance();
    final Coin minTxFee = getCoinMinTxFee();

    log.info("wallet spendable {}", walletBalanceSpendable.toFriendlyString());
    log.info("min fee {}", minTxFee.toFriendlyString());
    
    // on testnet seeing balance deficiency of .0000000xxx so accomodate for that by padding
    // spendable by twice the transaction fee
    Coin spendable = walletBalanceSpendable.minus(minTxFee.add(minTxFee));
    log.info("spendable - fee: {}", spendable.toFriendlyString());
    
    if (!confirmMinimumWalletAmount(spendable)) {
      log.info("insufficient funds: {}", spendable.toFriendlyString());
      return false;
    }

    SendRequest req = SendRequest.to(walletService.getLegacySendToAddress(addressStr), spendable);
    req.feePerKb = minTxFee;
    
    log.info("send to address {}", addressStr);
    log.info("fee {}", req.feePerKb.toFriendlyString());
    
    return walletService.sendMoney(req);
  }
  
  @Override
  public BalancePayment save(final BalancePayment order) {
    BalancePayment savedOrder = balancePaymentRepository.save(order);
    log.info("order {} saved to db.", savedOrder);
    return savedOrder;
  }
  
  private Coin getCoinMinTxFee() {
    return Transaction.REFERENCE_DEFAULT_MIN_TX_FEE;
  }
  
  private Coin convertBtcToSatoshis(final Double btc) {
    return Coin.valueOf((long)Math.floor(btc * 100000000));
  }

  private Double getMinWithdrawlInBtc(final Double dollarAmount) {
    Double amount = dollarAmount / this.bitcoinRateService.getBtcRate();
    log.info("min btc {}", amount);
    return amount;
  }
  
  private boolean confirmMinimumWalletAmount(final Coin spendable) {
    return spendable.isLessThan(convertBtcToSatoshis(getMinWithdrawlInBtc(this.minXferAmt))) ? false : true;
  }  
//  public Collection<BalancePayment> findByFromAddress(final String fromAddress) {
//    Collection<BalancePayment> balancePayments = balancePaymentRepository.findByFrom(fromAddress);
//    log.info("balance payments {} retrieved from db.", balancePayments);
//    return balancePayments;
//  }
//  
//  public Collection<BalancePayment> findByToAddress(final String toAddress) {
//    Collection<BalancePayment> balancePayments = balancePaymentRepository.findByTo(toAddress);
//    log.info("balance payments {} retrieved from db.", balancePayments);
//    return balancePayments;
//  }
  
}
