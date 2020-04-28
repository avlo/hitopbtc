package com.hitop.service.bitcoin;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
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
public class BitcoinRateService implements RateService {
  
  private static final String CURRENCY = "USD";
  private final String rateUrl;
  private final Double shippingPriceUsd;
  private final Double unitPriceUsd;
  
  public BitcoinRateService(
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
