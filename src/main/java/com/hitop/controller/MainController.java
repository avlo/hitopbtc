package com.hitop.controller;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;
import com.hitop.entity.HitopOrder;
import com.hitop.repository.HitopOrderRepository;
import com.hitop.service.CoinsReceivedService;
import com.hitop.service.EventNotifier;
import com.hitop.service.QRCodeService;
import com.hitop.service.RateService;
import com.hitop.service.WalletService;

@RestController
@RequestMapping(path="/")
public class MainController implements EventNotifier {
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

  private SseEmitter emitter;
  private SimpleDateFormat sdf;

  public MainController() throws Exception {
    sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
  }

  @GetMapping("/orderscreen")
  public ModelAndView displayOrderForm () throws Exception {
    walletService.monitorReceiveEvent(coinsReceivedService);
    coinsReceivedService.setEventNotifier(this);
    ModelAndView modelAndView = new ModelAndView("order");
    modelAndView.addObject("walletid", qrCodeService.getQRCodeUrl(walletService.getSendToAddress()));
    modelAndView.addObject("rate", String.format("%.9f", rateService.getUsdtoBtc(rateService.getBtcRate())));
    return modelAndView;
  }

  @GetMapping("/comment/stream")
  public SseEmitter feed() {
    logger.info("222222222222222");
    logger.info("222222222222222");
    logger.info("222222222222222");

    emitter = new SseEmitter();

    ExecutorService executor = Executors.newSingleThreadExecutor();
    executor.execute(() -> {
      try {
        for (int i = 0; true; i++) {
          SseEventBuilder event = SseEmitter.event()
              .data("{\"timestamp\":\"" + LocalTime.now().toString() + "\"}");
          emitter.send(event);
          Thread.sleep(1000);
        }
      } catch (Exception e) {
        emitter.completeWithError(e);
      }
    });
    executor.shutdown();
    return emitter;
  }

  public void sendEvent() {
    logger.info("33333333333333");
    logger.info("33333333333333");
    logger.info("33333333333333");
    Date dt = new Date();
    String dateStr = sdf.format(dt);
    logger.info(dateStr);
    
//    ExecutorService executor = Executors.newSingleThreadExecutor();
//    executor.execute(() -> {
//      try {
//        SseEventBuilder event = SseEmitter.event()
//            .data("{\"timestamp\":\"" + dateStr + "\"}");
//        emitter.send(event);
//        emitter.complete();
//      } catch(Exception e) {
//        emitter.completeWithError(e);
//      }
//    });
//    executor.shutdown();
  }

  @GetMapping(path="/all")
  public @ResponseBody Iterable<HitopOrder> getAllOrders() {
    // This returns a JSON or XML with the users
    return hitopOrderRepository.findAll();
  }
}
