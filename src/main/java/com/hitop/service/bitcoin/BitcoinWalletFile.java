package com.hitop.service.bitcoin;

import java.io.File;
import java.io.IOException;
import org.bitcoinj.wallet.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import com.hitop.service.WalletFile;

@Component
@ConditionalOnProperty(
    name = "spring.profiles.active", 
    havingValue = "test")
public class BitcoinWalletFile implements WalletFile {
  final Logger logger = LoggerFactory.getLogger(BitcoinWalletFile.class);
  
  private final File file;
  
  public BitcoinWalletFile(final @Value("${wallet.filename.prefix}") String filePrefix) {
    this.file = new File(filePrefix);
  }
  
  public void saveToFile(final Wallet wallet) {
    // Runs in the dedicated "user thread" (see bitcoinj docs for more info on this).
    // The transaction "tx" can either be pending, or included into a block (we didn't see the broadcast).
    try {
      logger.info("saving file {}...", file.toString());
      wallet.saveToFile(file);
      logger.info("{} saved.", file.toString());
    } catch (IOException e) {
      logger.info("{} save FAILED", file.toString());
      e.printStackTrace();
    }
  }
  
  public String getFilePrefix() {
    return this.file.getName();
  }
}
