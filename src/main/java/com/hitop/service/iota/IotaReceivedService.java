package com.hitop.service.iota;

import org.iota.jota.account.event.AccountEvent;
import org.iota.jota.account.event.events.EventPromotion;
import org.iota.jota.account.event.events.EventReceivedDeposit;
import org.iota.jota.account.event.events.EventTransferConfirmed;
import org.iota.jota.account.plugins.AccountPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.hitop.controller.ReceiptListener;

@Service
@ConditionalOnProperty(
    name = "spring.profiles.active", 
    havingValue = "test")
public class IotaReceivedService extends AccountPlugin  {
  private final static Logger log = LoggerFactory.getLogger(IotaReceivedService.class);

//  private final IotaWalletFile walletFile;
//  private final IotaFuturesCallback futuresCallback;
  private final IotaTransactionWrapper iotaTransactionWrapper;

  // TODO: below receiptListener is field injected (instead of constructor injected) because
  //       docker complains about circular dependency when using constructor injection
  @Autowired
  private ReceiptListener receiptListener;

  @Autowired
  public IotaReceivedService(
//      final IotaWalletFile walletFile,
//      final IotaFuturesCallback futuresCallback,
      final IotaTransactionWrapper iotaTransactionWrapper) {
//    this.walletFile = walletFile;
//    this.futuresCallback = futuresCallback;
    this.iotaTransactionWrapper = iotaTransactionWrapper;
  }

  @Override
  public void load() throws Exception {
    // Load data that the plugin needs such as reading a file, generating memory intensive resources, etc..
  }

  @Override
  public boolean start() {
    // Start any processes that you want to run continuously

    // Return true if all went well, otherwise return false
    return true;
  }

  @Override
  public void shutdown() {
    // Stop any running processes here
  }

  @AccountEvent
  public void confirmed(EventTransferConfirmed e) {
    System.out.println("account: " + this.getAccount().getId());
    System.out.println("confimed: " + e.getBundle().getBundleHash());
  }

  @AccountEvent
  public void promoted(EventPromotion e) {
    System.out.println("account: " + this.getAccount().getId());
    System.out.println("promoted: " + e.getPromotedBundle());
  }

  @AccountEvent
  public void received(EventReceivedDeposit e) {
    System.out.println("Account: " + this.getAccount().getId());
    System.out.println("Received a new payment: " + e.getBundle());

//    try {
//      walletFile.saveToFile(wallet);
//    } catch (IOException e) {
//      e.printStackTrace();
//      log.info("{} save FAILED.", walletFile.getFilePrefix());
//
//    }
    log.info("Received tx for {} : {}", e.getAmount(), e.getAddress().toString());
    log.info("account avail balance: {}", this.getAccount().availableBalance());
    log.info("account total balance: {}", this.getAccount().totalBalance());
    log.info("---");

    iotaTransactionWrapper.setTransaction(e);
//    futuresCallback.addCallback(iotaTransactionWrapper);

    // TODO: an outside payment to this address can cause this to fire, even though there's no UI listening for it
    receiptListener.displayReceiptSse(iotaTransactionWrapper);
  }

  @Override
  public String name() {
    StringBuilder sb = new StringBuilder();
    sb.append("**************");
    sb.append("**************");    
    sb.append("IotaAccountPlugin");
    sb.append("**************");
    sb.append("**************");
    return sb.toString();
  }
}
