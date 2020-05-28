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

  // TODO 85: enterprise version, refactor into OrderReposity and inject HitopOrderRepo on the fly
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
  
  private SseEmitter emitter;
  
  ExecutorService executor = Executors.newCachedThreadPool();

  public MainController() throws Exception {
  }

  @GetMapping("/orderdetails")
  public String getOrderDetails(Model model) throws Exception {
    walletService.addCoinsReceivedEventListener(coinReceivedService);
    coinReceivedService.addReceivedListener(this);
//      hitopOrder.setRateService.getUsdtoBtc(rateService.getBtcRate())));
    model.addAttribute("hitopOrder", hitopOrder);
    // TODO 30 : add above hitopOrder to html as formula to display dollar conversion
    return "orderdetails";
  }

  @PostMapping("/ordersubmit")
  public String displayQR(HitopOrder hitopOrder,
      BindingResult result, Model model) throws Exception {
    hitopOrder.setBtcPublicKey(qrCodeService.getQRCodeUrl(walletService.getSendToAddress()));
    this.hitopOrder = hitopOrder;
    System.out.println("11111111111111");
    System.out.println("11111111111111");
    System.out.println(this.hitopOrder.getName() + "\n\n");
    // TODO 90: uncomment when errors are implemented
//    if (result.hasErrors()) {
//      return "orderdetails";
//    }
    
   // TODO 20: move below into QR-code callback
    // orderServiceImpl.addNewOrder(hitopOrder);
    model.addAttribute(hitopOrder);
    // TODO 70 : call appropriate ordersubmit.html file based on stub/test/etc
    return "ordersubmit";
  }
  
  @GetMapping("/receipt")
  public String displayReceipt(Model model) throws Exception {
    model.addAttribute(orderServiceImpl.save(hitopOrder));
    return "receipt";
  }
  
  @GetMapping("/receipt-sse")
  public SseEmitter setupSSEEmitter() {
    this.emitter = new SseEmitter(1200000l);
    return this.emitter;
  }

  public HitopOrder displayReceiptSse() {
    executor.execute(() -> {
      try {
        SseEventBuilder event = SseEmitter.event()
            .data("{\"name\":\"" + hitopOrder.getName());
        // TODO 95: use hitopOrder instead of above getter call.  is this even doable when using SseEmitter?
        //       .data(hitopOrder);
        this.emitter.send(event);
        this.emitter.complete();
      } catch (Exception e) {
        logger.info("##### EMITTER EXCEPTION: " + e.toString());
        this.emitter.completeWithError(e);
      }
    });
    executor.shutdown();
    return hitopOrder;
  }

  //TODO 50 keep this but wrap it in security so only admin can call it
  @GetMapping(path="/allordershitop")
  public @ResponseBody Iterable<HitopOrder> getAllOrders() {
    // This returns a JSON or XML with the users
    return hitopOrderRepository.findAll();
  }
}
