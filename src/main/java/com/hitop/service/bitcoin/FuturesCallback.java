package com.hitop.service.bitcoin;

import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;

@Component
public class FuturesCallback {
  final Logger logger = LoggerFactory.getLogger(FuturesCallback.class);

  public void addCallback(final Transaction tx) {
    logger.info("entered FuturesCallback");
    // Wait until it's made it into the block chain (may run immediately if it's already there).
    //
    // For this dummy app of course, we could just forward the unconfirmed transaction. If it were
    // to be double spent, no harm done. Wallet.allowSpendingUnconfirmedTransactions() would have to
    // be called in onSetupCompleted() above. But we don't do that here to demonstrate the more common
    // case of waiting for a block.
    Futures.addCallback(tx.getConfidence().getDepthFuture(1), new FutureCallback<TransactionConfidence>() {
      @Override
      public void onSuccess(TransactionConfidence result) {
        // TODO 40 : this notification arrives ~5min after above "onCoinsReceived" event arrives.
        // it's the equivalent of single block confirmation, we can use this to update it's DB state
        final String crlf = System.getProperty("line.separator");
        final String val = crlf + 
            "*********************" + crlf +
            "Confirmation received" + crlf +
            "*********************"; 
        logger.info(val);
        // send user email notification of first confirmation received
      }
      
      @Override
      public void onFailure(Throwable t) {
        // This kind of future can't fail, just rethrow in case something weird happens.
        throw new RuntimeException(t);
      }
    }, MoreExecutors.directExecutor());
  }
}