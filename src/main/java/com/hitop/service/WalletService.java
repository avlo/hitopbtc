package com.hitop.service;

import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;

public interface WalletService {
  void addCoinsReceivedEventListener(WalletCoinsReceivedEventListener listener) throws Exception;
  String getSendToAddress();
}
