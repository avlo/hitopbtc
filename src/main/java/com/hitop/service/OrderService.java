package com.hitop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hitop.entity.HitopOrder;
import com.hitop.repository.HitopOrderRepository;

@Service
public class OrderService {
  Logger logger = LoggerFactory.getLogger(OrderService.class);
  
  @Autowired
  private HitopOrderRepository hitopOrderRepository;
  
  @Autowired
  private RateService btcRateService;

  public String addNewOrder () {
    logger.info("new order received");
    return "new order received";
  }
  
  public String addNewOrder (
      String name,
      String email,
      String address,
      String city,
      String state,
      String zip,
      String country,
      String btcPublicKey,
      String btcTransaction,
      Integer status) 
  {
    HitopOrder n = new HitopOrder();
    n.setName(name);
    n.setEmail(email);
    n.setAddress(address);
    n.setCity(city);
    n.setState(state);
    n.setZip(zip);
    n.setCountry(country);
    n.setBtcPublicKey(btcPublicKey);
    n.setBtcTransaction(btcTransaction);
    Double rate = btcRateService.getBtcRate();
    n.setBtcRate(rate);
    n.setBtcUsdAmount(btcRateService.getUsdtoBtc(rate));
    n.setStatus(status);
    hitopOrderRepository.save(n);
    return "Saved\n\n";
  }
}
