package com.hitop.service;

import java.io.File;
import java.io.IOException;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;

@Service
public class CoinsReceivedService implements WalletCoinsReceivedEventListener {
  final Logger logger = LoggerFactory.getLogger(CoinsReceivedService.class);

  private final OrderService orderService;
  private final String filePrefix;

  @Autowired
  public CoinsReceivedService(
      final OrderService orderService,
      final @Value("${wallet.filename.prefix}") String filePrefix) {
    
    this.orderService = orderService;
    this.filePrefix = filePrefix;
  }
  
  @Override
  public void onCoinsReceived(
      final Wallet wallet, 
      final Transaction tx, 
      final Coin prevBalance, 
      final Coin newBalance) {

    // Runs in the dedicated "user thread" (see bitcoinj docs for more info on this).
    // The transaction "tx" can either be pending, or included into a block (we didn't see the broadcast).
    try {
      File file = new File(filePrefix);
      logger.info("saving file {}...", file.toString());
      wallet.saveToFile(file);
      logger.info("{} saved.", file.toString());
    } catch (IOException e) {
      logger.info("SAVE FAILED");
      e.printStackTrace();
    }

    orderService.addNewOrder();

    Coin value = tx.getValueSentToMe(wallet);
    logger.info("Received tx for {} : {}", value.toFriendlyString(), tx);
    logger.info("new value: {}", wallet.getBalance().getValue());
    logger.info("---");
    logger.info("coin prev balance : {}", prevBalance.toFriendlyString());
    logger.info("coin new balance : {}", newBalance.toFriendlyString());
    // Wait until it's made it into the block chain (may run immediately if it's already there).
    //
    // For this dummy app of course, we could just forward the unconfirmed transaction. If it were
    // to be double spent, no harm done. Wallet.allowSpendingUnconfirmedTransactions() would have to
    // be called in onSetupCompleted() above. But we don't do that here to demonstrate the more common
    // case of waiting for a block.
    Futures.addCallback(tx.getConfidence().getDepthFuture(1), new FutureCallback<TransactionConfidence>() {
      @Override
      public void onSuccess(TransactionConfidence result) {
        logger.info("********************* Confirmation received *********************");
      }

      @Override
      public void onFailure(Throwable t) {
        // This kind of future can't fail, just rethrow in case something weird happens.
        throw new RuntimeException(t);
      }
    }, MoreExecutors.directExecutor());
  }
}
