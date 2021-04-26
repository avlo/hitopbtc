package com.hitop.service.iota;

import org.iota.jota.account.event.events.EventReceivedDeposit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import com.hitop.service.TransactionWrapper;

@Component
@ConditionalOnProperty(
    name = "spring.profiles.active",
    havingValue = "test")
public class IotaTransactionWrapper implements TransactionWrapper {
  private final static Logger log = LoggerFactory.getLogger(IotaTransactionWrapper.class);

  private EventReceivedDeposit eventReceivedDeposit;

  @Autowired
  public IotaTransactionWrapper() {
  }
  
  public void setTransaction(final EventReceivedDeposit e) {
    this.eventReceivedDeposit = e;
  }

//  public ListenableFuture<TransactionConfidence> getDepthFuture() {
//    return transaction.getConfidence().getDepthFuture(1);
//  }

  @Override
  public String getTxReceiveAddress() {
    // TODO: iota needs proper way to receive address
    return this.eventReceivedDeposit.getAddress().toString();
  }
}
