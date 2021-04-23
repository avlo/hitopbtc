package com.hitop.service.bitcoin;

import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.kits.WalletAppKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import com.google.common.util.concurrent.ListenableFuture;
import com.hitop.NetworkParameters;
import com.hitop.service.TransactionWrapper;

@Component
@ConditionalOnProperty(
    name = "spring.profiles.active",
    havingValue = "test")
public class BitcoinTransactionWrapper implements TransactionWrapper {
  private final static Logger log = LoggerFactory.getLogger(BitcoinTransactionWrapper.class);

  private Transaction transaction;
  private final WalletAppKit walletAppKit;
  private final NetworkParameters parameters;

  @Autowired
  public BitcoinTransactionWrapper(
      final NetworkParameters params,
      final WalletAppKit walletAppKit) {
    this.parameters = params;
    this.walletAppKit = walletAppKit;
  }
  
  public void setTransaction(final Transaction transaction) {
    this.transaction = transaction;
  }

  @Override
  public ListenableFuture<TransactionConfidence> getDepthFuture() {
    return transaction.getConfidence().getDepthFuture(1);
  }

  @Override
  public String getTxReceiveAddress() {
    // TODO: replace for/if with functional functionalIF / lambda
    for(TransactionOutput txo : transaction.getOutputs()){
      if (txo.isMine(walletAppKit.wallet())) {
        String walletTxo = txo.getScriptPubKey().getToAddress(parameters.getNetworkParameters(), true).toString();
        log.info("wallet txo: {}", walletTxo);
        return walletTxo;
      }
    }
    log.error("TXO not found in our wallet");
    // TODO: needs exception handling/wrapper
    return "****** REPLACE THIS STRING WITH \"TXO NOT IN OUR WALLET\" EXCEPTION ************";
  }
}
