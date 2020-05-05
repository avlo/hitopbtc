package com.hitop.service.stub;

import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.hitop.service.WalletService;

@Service
@ConditionalOnProperty(
    name = "spring.profiles.active", 
    havingValue = "dev")
public class StubWalletService implements WalletService {
  final Logger logger = LoggerFactory.getLogger(StubWalletService.class);
  
  private StubReceivedService stubReceivedService;
  
  public StubWalletService()  { }

  @Override
  public void addCoinsReceivedEventListener(final WalletCoinsReceivedEventListener listener) throws Exception {
    
  }

  @Override
  public String getSendToAddress() {
    return "STUB WALLET";
  }
}
