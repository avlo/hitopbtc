package com.hitop.service.stub;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.wallet.Wallet;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.hitop.controller.ReceiptListener;
import com.hitop.service.CoinReceivedService;

@Service
@ConditionalOnProperty(
    name = "spring.profiles.active", 
    havingValue = "dev")
public class StubReceivedService implements CoinReceivedService {

  private ReceiptListener receiptListener;
  
  @Override
  public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
    receiptListener.displayReceiptSse();
  }
  
  public void addReceivedListener(ReceiptListener receiptListener) {
    this.receiptListener = receiptListener;
  }

}
