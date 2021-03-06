package com.hitop.service.litecoin;

/*
 *  Copyright 2020 Nick Avlonitis
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.hitop.service.QRCodeService;
import com.hitop.service.RateService;

@Service
@ConditionalOnProperty(
    name = "spring.profiles.active",
    havingValue = "test")
@ConditionalOnExpression("${litecoin.bean:false}")
public class LitecoinQRCodeService implements QRCodeService {
  private final static Logger log = LoggerFactory.getLogger(LitecoinQRCodeService.class);

  private final RateService litecoinRateService;
  private final String decimalPrecision;
  private final String qrUrl;

  @Autowired
  public LitecoinQRCodeService(
      final RateService litecoinRateService,
      final @Value("${decimalprecision}") String decimalPrecision,
      final @Value("${qrurl.litecoin}") String qrUrl) {

    this.litecoinRateService = litecoinRateService;
    this.decimalPrecision = decimalPrecision;
    this.qrUrl = qrUrl;
  }
  
  @Override
  public String getQRCodeUrl(final String sendToAddress) {
    String btcValue = String.format(decimalPrecision, litecoinRateService.getUsdtoBtc(litecoinRateService.getBtcRate()));
    String qrCodeUrl = String.format(qrUrl, sendToAddress, btcValue);

    log.info("Send coins to: {} ", sendToAddress);
    log.info("QR-Code URL: {}", qrCodeUrl);
    log.info("Waiting for coins to arrive. Press Ctrl-C to quit.");

    return qrCodeUrl;
  }
}