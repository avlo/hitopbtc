package com.hitop.service;

import org.bitcoinj.core.TransactionConfidence;
import com.google.common.util.concurrent.ListenableFuture;

public interface TransactionWrapper {
  ListenableFuture<TransactionConfidence> getDepthFuture();
  String getTxReceiveAddress();
}