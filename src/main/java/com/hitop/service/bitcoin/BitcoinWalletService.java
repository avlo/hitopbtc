package com.hitop.service.bitcoin;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.SegwitAddress;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.hitop.NetworkParameters;
import com.hitop.service.WalletService;

@Service
@ConditionalOnProperty(
    name = "spring.profiles.active", 
    havingValue = "test")
public class BitcoinWalletService implements WalletService {
  private final static Logger log = LoggerFactory.getLogger(BitcoinWalletService.class);

  private final NetworkParameters parameters;
  private final WalletAppKit walletAppKit;

  @Autowired
  public BitcoinWalletService(
      final NetworkParameters params,
      final WalletAppKit walletAppKit) throws Exception {
    this.parameters = params;
    this.walletAppKit = walletAppKit;
  }

  @Override
  public String getTxReceiveAddress(final Transaction tx) {
    // TODO: replace for/if with functional functionalIF / lambda
//    Context.propagate(new Context(this.parameters.getNetworkParameters()));
    for(TransactionOutput txo : tx.getOutputs()){
      if (txo.isMine(walletAppKit.wallet())) {
        String walletTxo = txo.getScriptPubKey().getToAddress(this.parameters.getNetworkParameters(), true).toString();
        log.info("wallet txo: {}", walletTxo);
        return walletTxo;
      }
    }
    log.error("TXO not found in our wallet");
    // TODO: needs exception handling/wrapper
    return "****** REPLACE THIS STRING WITH \"TXO NOT IN OUR WALLET\" EXCEPTION ************";
  }

  @Override
  public void addCoinsReceivedEventListener(final WalletCoinsReceivedEventListener listener) {
    walletAppKit.wallet().addCoinsReceivedEventListener(listener);
  }

  @Override
  public String getFreshSendToAddress() {
    // below line needed or else throws context exception 
//    Context.propagate(new Context(this.parameters.getNetworkParameters()));
    
    // TODO: issue w/ Segwit, replace when fixed
    return LegacyAddress.fromKey(this.parameters.getNetworkParameters(), walletAppKit.wallet().freshReceiveKey()).toString();
  }
  
  @Override
  public Address getLegacySendToAddress(final String address) {
//    Context.propagate(new Context(this.parameters.getNetworkParameters()));
    return LegacyAddress.fromString(this.parameters.getNetworkParameters(), address);
  }
  
  // TODO: issue w/ Segwit, replace when fixed
  public Address getSegwitSendToAddress(final String address) {
    return SegwitAddress.fromString(this.parameters.getNetworkParameters(), address);
  }

  @Override
  public String getBalance() {
    return getCoinBalance().toFriendlyString();
  }

  @Override
  public Coin getCoinBalance() {
    walletAppKit.wallet().cleanup();
    return walletAppKit.wallet().getBalance();
  }
  
  @Override
  public boolean sendMoney(final SendRequest req) {
//    Context.propagate(new Context(this.parameters.getNetworkParameters()));
    try {
      walletAppKit.wallet().sendCoins(req);
      return true;
    } catch (InsufficientMoneyException e) {
      log.info("************************************************************************");
      log.info("************* SHOULD NOT OCCUR SINGE WE'RE CHECKING FOR THIS ***********");
      log.info("************************************************************************");
      log.info("************************************************************************");
      log.info("exception getMessage() {}", e.getMessage());
      log.info("************************************************************************");
      log.info("exception getLocalizedMessage() {}", e.getLocalizedMessage());
      log.info("************************************************************************");
      log.info("exception toFriendlyString() {}", e.missing.toFriendlyString());
      log.info("************************************************************************");
      return false;
    }
  }
}
