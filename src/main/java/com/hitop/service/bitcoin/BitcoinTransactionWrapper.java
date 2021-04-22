package com.hitop.service.bitcoin;

import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.util.concurrent.ListenableFuture;
import com.hitop.service.TransactionWrapper;

public class BitcoinTransactionWrapper implements TransactionWrapper {
  private final static Logger log = LoggerFactory.getLogger(BitcoinTransactionWrapper.class);

  final Transaction transaction;
  
  public BitcoinTransactionWrapper(final Transaction transaction) {
    this.transaction = transaction;
  }
  
  @Override
  public ListenableFuture<TransactionConfidence> getDepthFuture() {
    return transaction.getConfidence().getDepthFuture(1);
  }

  @Override
  public Transaction getTransaction() {
    return transaction;
  }
}
