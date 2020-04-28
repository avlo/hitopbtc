package com.hitop.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hitop.entity.HitopOrder;
import com.hitop.repository.HitopOrderRepository;

@Service
public class OrderServiceImpl {
  Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
  
  private final HitopOrderRepository hitopOrderRepository;
  private final RateService rateService;
  
  @Autowired
  public OrderServiceImpl(
      final HitopOrderRepository hitopOrderRepository,
      final RateService rateService) {
    
    this.hitopOrderRepository = hitopOrderRepository;
    this.rateService = rateService;
  }
  
  public HitopOrder addNewOrder(HitopOrder hitopOrder) {
    Double rate = rateService.getBtcRate();  // TODO: should use rate sent to page
    hitopOrder.setBtcRate(rate);
    hitopOrder.setBtcUsdAmount(rateService.getUsdtoBtc(rate));
//    order.setStatus(status);
    HitopOrder savedOrder = hitopOrderRepository.save(hitopOrder);
    String out = String.format("order %s saved to db.", savedOrder);
    logger.info(out);
//    out = String.format("btc transaction %s saved to wallet.", btcTransaction);
    logger.info(out);
    return savedOrder;
  }
}
