package com.hitop.controller;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.hitop.entity.HitopOrder;
import com.hitop.repository.HitopOrderRepository;
import com.hitop.service.CoinsReceivedService;
import com.hitop.service.QRCodeService;
import com.hitop.service.RateService;
import com.hitop.service.WalletService;
import reactor.core.publisher.Flux;

@Controller
@RequestMapping(path="/")
public class MainController {
  final Logger logger = LoggerFactory.getLogger(MainController.class);
	
  @Autowired
  private HitopOrderRepository hitopOrderRepository;
  
  @Autowired
  private WalletService walletService;
  
  @Autowired
  private CoinsReceivedService coinsReceivedService;
  
  @Autowired
  private QRCodeService qrCodeService;
  
  @Autowired
  private RateService rateService;
  
  public MainController() throws Exception {
//    getTransactionJson("");
  }
  
  @GetMapping(path="/orderscreen")
  public ModelAndView displayOrderForm () throws Exception {
    walletService.monitorReceiveEvent(coinsReceivedService);
    ModelAndView modelAndView = new ModelAndView("order");
    modelAndView.addObject("walletid", qrCodeService.getQRCodeUrl(walletService.getSendToAddress()));
    modelAndView.addObject("rate", String.format("%.9f", rateService.getUsdtoBtc(rateService.getBtcRate())));
    return modelAndView;
  }
  
  @GetMapping(path = "/comment/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<String> feed() {
    return Flux.interval(Duration.ofSeconds(1))
        .onBackpressureDrop()
        .map(this::generateComment)
        .flatMapIterable(x -> x);
  }

  private List<String> generateComment(long o) {
    return Arrays.asList("55555555555555");
  }
  
  @GetMapping(path="/all")
  public @ResponseBody Iterable<HitopOrder> getAllOrders() {
    // This returns a JSON or XML with the users
    return hitopOrderRepository.findAll();
  }
}
