package com.hitop.service;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;


public interface CoinReceivedService extends WalletCoinsReceivedEventListener {

  void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance);

}
