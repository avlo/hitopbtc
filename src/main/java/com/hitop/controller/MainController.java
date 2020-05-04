package com.hitop.controller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;
import com.hitop.entity.HitopOrder;
import com.hitop.repository.HitopOrderRepository;
import com.hitop.service.CoinReceivedService;
import com.hitop.service.OrderServiceImpl;
import com.hitop.service.QRCodeService;
import com.hitop.service.RateService;
import com.hitop.service.WalletService;

@Controller
@RequestMapping(path="/")
public class MainController implements ReceiptListener {
  final Logger logger = LoggerFactory.getLogger(MainController.class);

  // TODO: enterprise version, refactor into OrderReposity and inject HitopOrderRepo on the fly
  @Autowired
  private HitopOrderRepository hitopOrderRepository;

  @Autowired
  private WalletService walletService;

  @Autowired
  private CoinReceivedService coinReceivedService;

  @Autowired
  private QRCodeService qrCodeService;

  @Autowired
  private RateService rateService;
  
  @Autowired
  private OrderServiceImpl orderServiceImpl;
  
  @Autowired
  private HitopOrder hitopOrder;

  @Autowired
  private SseEmitter emitter;

  public MainController() throws Exception {
  }

  @GetMapping("/orderdetails")
  public String getOrderDetails(Model model) throws Exception {
    coinReceivedService.addReceivedListener(this);
    walletService.monitorReceiveEvent(coinReceivedService);
//      hitopOrder.setRateService.getUsdtoBtc(rateService.getBtcRate())));
    model.addAttribute("hitopOrder", hitopOrder);
    // TODO: add above hitopOrder to html as formula to display dollar conversion
    return "orderdetails";
  }

  // just displays stuff for user before purchase
  @PostMapping("/ordersubmit")
  public String displayQR(HitopOrder hitopOrder,
      BindingResult result, Model model) throws Exception {
    hitopOrder.setBtcPublicKey(qrCodeService.getQRCodeUrl(walletService.getSendToAddress()));
    System.out.println(hitopOrder.getName());
    System.out.println(hitopOrder.getBtcPublicKey());
    
    // TODO: uncomment when errors are implemented
//    if (result.hasErrors()) {
//      return "orderdetails";
//    }
    
   // TODO: move below into QR-code callback
    // orderServiceImpl.addNewOrder(hitopOrder);
    model.addAttribute(hitopOrder);
    // TODO: call appropriate ordersubmit.html file based on stub/test/etc
    // TODO: future refactor to be pluggable qrcode or stub button
    return "ordersubmit";
  }
  
  // SSE Callback method, to be setup in ordersubmit.html
  @GetMapping("/receipt")
  public String displayReceipt(Model model) throws Exception {
    model.addAttribute(hitopOrder);
    
    orderServiceImpl.save(hitopOrder);
    
    System.out.println("22222222222");
    System.out.println("22222222222");
    System.out.println("22222222222");
    System.out.println(hitopOrder.getName());
    System.out.println("22222222222");
    
    return "receipt";
  }
  
  @GetMapping("/receipt-sse")
  public SseEmitter setupSSEEmitter() {
    logger.info("333333333333333");
    logger.info("333333333333333");
    logger.info("333333333333333");

    return emitter;
  }
  
  public void displayReceiptSse() {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    executor.execute(() -> {
      try {
        SseEventBuilder event = SseEmitter.event()
            .data("{\"name\":\"" + hitopOrder.getName() + " XXXXXXXXX\"}");
        // TODO: use hitopOrder instead of above getter call
//              .data(hitopOrder);
        emitter.send(event);
      } catch (Exception e) {
        emitter.completeWithError(e);
      }
    });
    executor.shutdown();
  }

  //TODO keep this but wrap it in security so only admin can call it
  @GetMapping(path="/allordershitop")
  public @ResponseBody Iterable<HitopOrder> getAllOrders() {
    // This returns a JSON or XML with the users
    return hitopOrderRepository.findAll();
  }
}
