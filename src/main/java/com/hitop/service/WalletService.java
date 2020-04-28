package com.hitop.service;

import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;

public interface WalletService {
  void monitorReceiveEvent(WalletCoinsReceivedEventListener listener) throws Exception;
  String getSendToAddress();
}
