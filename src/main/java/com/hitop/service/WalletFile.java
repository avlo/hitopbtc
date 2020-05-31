package com.hitop.service;

import org.bitcoinj.wallet.Wallet;

public interface WalletFile {
  void saveToFile(final Wallet wallet);
  String getFilePrefix();
}
