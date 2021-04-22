package com.hitop.service;

import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import com.google.common.util.concurrent.ListenableFuture;

public interface TransactionWrapper {
  ListenableFuture<TransactionConfidence> getDepthFuture();
  Transaction getTransaction();
}