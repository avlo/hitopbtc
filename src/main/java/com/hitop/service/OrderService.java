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
    final String crlf = System.getProperty("line.separator");
    String out = crlf +
        "**************" + crlf +
        "PLACEHOLDER" + crlf +
        "call overloaded addNewOrder(params) when ready" + crlf +
        "**************";
    logger.info(out);
    return out;
  }
  
  public String addNewOrder (
      final String name,
      final String email,
      final String address,
      final String city,
      final String state,
      final String zip,
      final String country,
      final String btcPublicKey,
      final String btcTransaction,
      final Integer status) 
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
    String out = String.format("transaction %s saved.", btcTransaction);
    logger.info(out);
    return out;
  }
}
