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

import org.litecoinj.core.TransactionConfidence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;

@Component
@ConditionalOnExpression("${litecoin.bean:false}")
public class LitecoinFuturesCallback {
  private final static Logger log = LoggerFactory.getLogger(LitecoinFuturesCallback.class);

  public void addCallback(final LitecoinTransactionWrapper tx) {
    log.info("entered Litecoin FuturesCallback");
    // Wait until it's made it into the block chain (may run immediately if it's already there).
    //
    // For this dummy app of course, we could just forward the unconfirmed transaction. If it were
    // to be double spent, no harm done. Wallet.allowSpendingUnconfirmedTransactions() would have to
    // be called in onSetupCompleted() above. But we don't do that here to demonstrate the more common
    // case of waiting for a block.
    Futures.addCallback(tx.getDepthFuture(), new FutureCallback<TransactionConfidence>() {
      @Override
      public void onSuccess(final TransactionConfidence result) {
        // TODO 40 : this notification arrives ~5min after above "onCoinsReceived" event arrives.
        // it's the equivalent of single block confirmation, we can use this to update it's DB state
        final String crlf = System.getProperty("line.separator");
        final String val = crlf +
            "*********************" + crlf +
            "LTC Confirmation received" + crlf +
            "*********************";
        log.info(val);
        // send user email notification of first confirmation received
      }

      @Override
      public void onFailure(Throwable t) {
        // This kind of future can't fail, just rethrow in case something weird happens.
        throw new RuntimeException(t);
      }
    }, MoreExecutors.directExecutor());
  }
}
