package com.hitop.service.litecoin;

import org.litecoinj.core.Transaction;
import org.litecoinj.core.TransactionConfidence;
import org.litecoinj.core.TransactionOutput;
import org.litecoinj.kits.WalletAppKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import com.google.common.util.concurrent.ListenableFuture;
import com.hitop.service.litecoin.LitecoinNetworkParameters;
import com.hitop.service.TransactionWrapper;

@Component
@ConditionalOnProperty(
    name = "spring.profiles.active",
    havingValue = "test")
public class LitecoinTransactionWrapper implements TransactionWrapper {
  private final static Logger log = LoggerFactory.getLogger(LitecoinTransactionWrapper.class);

  private Transaction transaction;
  private final WalletAppKit litecoinWalletAppKit;
  private final LitecoinNetworkParameters litecoinNetworkParameters;

  @Autowired
  public LitecoinTransactionWrapper(
      final LitecoinNetworkParameters litecoinNetworkParameters,
      final WalletAppKit litecoinWalletAppKit) {
    this.litecoinNetworkParameters = litecoinNetworkParameters;
    this.litecoinWalletAppKit = litecoinWalletAppKit;
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
      if (txo.isMine(litecoinWalletAppKit.wallet())) {
        String walletTxo = txo.getScriptPubKey().getToAddress(litecoinNetworkParameters.getNetworkParameters(), true).toString();
        log.info("wallet txo: {}", walletTxo);
        return walletTxo;
      }
    }
    log.error("TXO not found in our wallet");
    // TODO: needs exception handling/wrapper
    return "****** REPLACE THIS STRING WITH \"TXO NOT IN OUR WALLET\" EXCEPTION ************";
  }
}
