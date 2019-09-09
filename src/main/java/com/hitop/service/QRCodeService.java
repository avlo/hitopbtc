package com.hitop.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class QRCodeService {
  final Logger logger = LoggerFactory.getLogger(QRCodeService.class);
  
  private RateService rateService;
  private final String bitcoinDigitFormat;
  private final String qrUrl;
  
  @Autowired
  public QRCodeService(
      final RateService rateService,
      final @Value("${bitcoinformat}") String bitcoinDigitFormat,
      final @Value("${qrurl}") String qrUrl) {
    
    this.rateService = rateService;
    this.bitcoinDigitFormat = bitcoinDigitFormat;
    this.qrUrl = qrUrl;
  }
  
  public String getQRCodeUrl(final String sendToAddress) {
    String btcValue = String.format(bitcoinDigitFormat, rateService.getUsdtoBtc(rateService.getBtcRate()));
    String qrCodeUrl = String.format(qrUrl, sendToAddress, btcValue);

    logger.info("Send coins to: {} ", sendToAddress);
    logger.info("QR-Code URL: {}", qrCodeUrl);
    logger.info("Waiting for coins to arrive. Press Ctrl-C to quit.");

    return qrCodeUrl;
  }
}