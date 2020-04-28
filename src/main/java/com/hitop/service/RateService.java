package com.hitop.service;

public interface RateService {
  Double getBtcRate();
  Double getUsdtoBtc(Double btcRate);
}
