package com.hitop.service.bitcoin;

import java.io.File;
import java.io.IOException;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.wallet.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import com.hitop.controller.ReceiptListener;
import com.hitop.service.CoinReceivedService;
import com.hitop.service.OrderServiceImpl;

@Service
@ConditionalOnProperty(
    name = "spring.profiles.active", 
    havingValue = "test")
public class BitcoinReceivedService implements CoinReceivedService {
  final Logger logger = LoggerFactory.getLogger(BitcoinReceivedService.class);

  private final File file;
  private ReceiptListener receiptListener;

  @Autowired
  public BitcoinReceivedService(
      final OrderServiceImpl orderServiceImpl,
      final @Value("${wallet.filename.prefix}") String filePrefix) {
    
    this.file = new File(filePrefix);
  }
  
  public void addReceivedListener(ReceiptListener receiptListener) {
    this.receiptListener = receiptListener;
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
      logger.info("saving file {}...", file.toString());
      wallet.saveToFile(file);
      logger.info("{} saved.", file.toString());
    } catch (IOException e) {
      logger.info("{} save FAILED", file.toString());
      e.printStackTrace();
    }

    // TODO: orderService here should probably be more generic (listener/etc) and
    // also transactional- relocated into callback, below or try/catch above
//    orderServiceImpl.addNewOrder();

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
        // TODO: this notification arrives ~5min after above "onCoinsReceived" event arrives.
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
    
    receiptListener.displayReceiptSse();
  }
}
