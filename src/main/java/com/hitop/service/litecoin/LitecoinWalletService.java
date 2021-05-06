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

import javax.annotation.PostConstruct;
import org.litecoinj.core.SegwitAddress;
import org.litecoinj.kits.WalletAppKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.hitop.service.TransactionWrapper;
import com.hitop.service.WalletService;

@Service
@ConditionalOnProperty(
    name = "spring.profiles.active",
    havingValue = "test")
@ConditionalOnExpression("${litecoin.bean:false}")
public class LitecoinWalletService implements WalletService {
  private final static Logger log = LoggerFactory.getLogger(LitecoinWalletService.class);

  private final WalletAppKit litecoinWalletAppKit;
  private final LitecoinNetworkParameters litecoinNetworkParameters;
  private final LitecoinReceivedService litecoinReceivedService;

  @Autowired
  public LitecoinWalletService(
      final LitecoinNetworkParameters litecoinNetworkParameters,
      final WalletAppKit litecoinWalletAppKit,
      final LitecoinReceivedService litecoinReceivedService) throws Exception {
    log.debug("LitecoinWalletService ctor()");
    this.litecoinNetworkParameters = litecoinNetworkParameters;
    this.litecoinWalletAppKit = litecoinWalletAppKit;
    this.litecoinReceivedService = litecoinReceivedService;
  }

  @PostConstruct
  private void postConstruct() {
    log.debug("postConstruct()");
    this.litecoinWalletAppKit.wallet().addCoinsReceivedEventListener(litecoinReceivedService);
  }

  @Override
  public String getTxReceiveAddress(final TransactionWrapper tx) {
    return tx.getTxReceiveAddress();
  }

  @Override
  public String getFreshSendToAddress() {
    return SegwitAddress.fromKey(this.litecoinNetworkParameters.getNetworkParameters(), litecoinWalletAppKit.wallet().freshReceiveKey()).toString();
  }
}
