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

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.hitop.ExchangeRate;
import com.hitop.service.RateService;

@Service
@ConditionalOnProperty(
    name = "spring.profiles.active", 
    havingValue = "test")
public class LitecoinRateService implements RateService {
  
  private static final String CURRENCY = "USD";
  private final String rateUrl;
  private final Double shippingPriceUsd;
  private final Double unitPriceUsd;
  
  public LitecoinRateService(
      final @Value("${rateurl}") String rateUrl,
      final @Value("${shipping.price.usd}") Double shippingPriceUsd,
      final @Value("${unit.price.usd}") Double unitPriceUsd) {
    
    this.rateUrl = rateUrl;
    this.shippingPriceUsd = shippingPriceUsd;
    this.unitPriceUsd = unitPriceUsd;
  }
  
  @Override
  public Double getBtcRate() {
    ResponseEntity<List<ExchangeRate>> rateResponse =
        new RestTemplate().exchange(
            rateUrl,
            HttpMethod.GET,
            null, 
            new ParameterizedTypeReference<List<ExchangeRate>>() {}
            );

    List<ExchangeRate> rates = rateResponse.getBody();
    ExchangeRate exchangeRate = rates.stream().filter(currency -> CURRENCY.equals(currency.getCode())).findFirst().orElse(null);
    return exchangeRate.getRate();
  }
  
  @Override
  public Double getUsdtoBtc(final Double btcRate) {
    return (unitPriceUsd + shippingPriceUsd) / btcRate;
  }  
}
