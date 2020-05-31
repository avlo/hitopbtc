package com.hitop.service.bitcoin;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.wallet.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.hitop.controller.ReceiptListener;
import com.hitop.service.CoinReceivedService;
import com.hitop.service.WalletFile;

@Service
@ConditionalOnProperty(
    name = "spring.profiles.active", 
    havingValue = "test")
public class BitcoinReceivedService implements CoinReceivedService {
  final Logger logger = LoggerFactory.getLogger(BitcoinReceivedService.class);

  private final BitcoinWalletFile walletFile;
  private final ReceiptListener receiptListener;
  private final FuturesCallback futuresCallback;
  
  @Autowired
  public BitcoinReceivedService(final BitcoinWalletFile walletFile, final ReceiptListener receiptListener, final FuturesCallback futuresCallback) {
    this.walletFile = walletFile;
    this.receiptListener = receiptListener;
    this.futuresCallback = futuresCallback;
  }

  @Override
  public void onCoinsReceived(
      final Wallet wallet, 
      final Transaction tx, 
      final Coin prevBalance, 
      final Coin newBalance) {

    walletFile.saveToFile(wallet);

    logger.info("Received tx for {} : {}", tx.getValueSentToMe(wallet).toFriendlyString(), tx);
    logger.info("new value: {}", wallet.getBalance().getValue());
    logger.info("---");
    logger.info("coin prev balance : {}", prevBalance.toFriendlyString());
    logger.info("coin new balance : {}", newBalance.toFriendlyString());

    futuresCallback.addCallback(tx);
    receiptListener.displayReceiptSse();
  }
}
