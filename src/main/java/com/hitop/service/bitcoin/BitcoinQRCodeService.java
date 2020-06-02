package com.hitop.service.bitcoin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.hitop.service.QRCodeService;
import com.hitop.service.RateService;

@Service
@ConditionalOnProperty(
    name = "spring.profiles.active", 
    havingValue = "test")
public class BitcoinQRCodeService implements QRCodeService {
  final Logger logger = LoggerFactory.getLogger(BitcoinQRCodeService.class);
  
  @Qualifier("bitcoinRateService")
  private RateService bitcoinRateService;
  private final String decimalPrecision;
  private final String qrUrl;
  
  @Autowired
  public BitcoinQRCodeService(
      final RateService bitcoinRateService,
      final @Value("${decimalprecision}") String decimalPrecision,
      final @Value("${qrurl}") String qrUrl) {
    
    this.bitcoinRateService = bitcoinRateService;
    this.decimalPrecision = decimalPrecision;
    this.qrUrl = qrUrl;
  }
  
  @Override
  public String getQRCodeUrl(final String sendToAddress) {
    String btcValue = String.format(decimalPrecision, bitcoinRateService.getUsdtoBtc(bitcoinRateService.getBtcRate()));
    String qrCodeUrl = String.format(qrUrl, sendToAddress, btcValue);

    logger.info("Send coins to: {} ", sendToAddress);
    logger.info("QR-Code URL: {}", qrCodeUrl);
    logger.info("Waiting for coins to arrive. Press Ctrl-C to quit.");

    return qrCodeUrl;
  }
}