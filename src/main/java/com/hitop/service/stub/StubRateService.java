package com.hitop.service.stub;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.hitop.service.RateService;

@Service
@ConditionalOnProperty(
    name = "spring.profiles.active", 
    havingValue = "dev")
public class StubRateService implements RateService {

  @Override
  public Double getBtcRate() {
    return Double.valueOf(0.1234);
  }

  @Override
  public Double getUsdtoBtc(Double btcRate) {
    return Double.valueOf(0.5678);
  }

}
