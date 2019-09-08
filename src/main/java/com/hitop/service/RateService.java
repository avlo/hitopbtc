package com.hitop.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.hitop.ExchangeRate;

@Service
public class RateService {
  
  @Value("${rateurl}")
  private String rateUrl;
  
  @Value("${shipping.price.usd}")
  private Double shippingPriceUsd;
  
  @Value("${unit.price.usd}")
  private Double unitPriceUsd;
      
  private static final String CURRENCY = "USD";
  
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
  
  public Double getUsdtoBtc(final Double btcRate) {
    return (unitPriceUsd + shippingPriceUsd) / btcRate;
  }  
}
