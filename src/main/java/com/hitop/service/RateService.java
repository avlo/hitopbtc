package com.hitop.service;

import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.hitop.ExchangeRate;

@Service
public class RateService {
  private static final String RATE_URL = "https://bitpay.com/api/rates";
  private static final String CURRENCY = "USD";
  private static final double SHIPPING_PRICE_USD = 0.05;
  private static final double UNIT_PRICE_USD = 0.10;
      
  public Double getBtcRate() {
    ResponseEntity<List<ExchangeRate>> rateResponse =
        new RestTemplate().exchange(
            RATE_URL,
            HttpMethod.GET,
            null, 
            new ParameterizedTypeReference<List<ExchangeRate>>() {}
            );

    List<ExchangeRate> rates = rateResponse.getBody();
    ExchangeRate exchangeRate = rates.stream().filter(currency -> CURRENCY.equals(currency.getCode())).findFirst().orElse(null);
    return exchangeRate.getRate();
  }
  
  public Double getUsdtoBtc(final Double btcRate) {
    return (UNIT_PRICE_USD + SHIPPING_PRICE_USD) / btcRate;
  }  
}
