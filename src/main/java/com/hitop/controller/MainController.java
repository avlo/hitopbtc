package com.hitop.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import com.hitop.entity.HitopOrder;
import com.hitop.repository.HitopOrderRepository;
import com.hitop.service.CoinReceivedService;
import com.hitop.service.OrderServiceImpl;
import com.hitop.service.QRCodeService;
import com.hitop.service.RateService;
import com.hitop.service.WalletService;

@RestController
@RequestMapping(path="/")
public class MainController {
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

  public MainController() throws Exception {
  }

  @GetMapping("/orderform")
  public ModelAndView displayOrderForm () throws Exception {
    walletService.monitorReceiveEvent(coinReceivedService);
    ModelAndView modelAndView = new ModelAndView("order");
    modelAndView.addObject("walletid", qrCodeService.getQRCodeUrl(walletService.getSendToAddress()));
    // TODO: add back in to display dollar conversion
    // modelAndView.addObject("rate", String.format("%.9f", rateService.getUsdtoBtc(rateService.getBtcRate())));
    return modelAndView;
  }

  @PostMapping("/submit")
  public ModelAndView submitOrder(@RequestBody HitopOrder hitopOrder,
      BindingResult result, Model model) throws Exception {
    
    if (result.hasErrors()) {
      //TODO: originally per https://www.baeldung.com/spring-boot-crud-thymeleaf
//      return "add-user";  
      return new ModelAndView("order");
    }
    
    orderServiceImpl.addNewOrder(hitopOrder);
    ModelAndView modelAndView = new ModelAndView("receipt");
    // TODO: originally per https://www.baeldung.com/spring-boot-crud-thymeleaf
//  userRepository.save(user);
//  model.addAttribute("users", userRepository.findAll());
//  return "index";
    
    // TODO: return populated hitopOrder object to receipt page
    
    return modelAndView;
  }

  //TODO keep this but wrap it in security so only admin can call it
  @GetMapping(path="/allordershitop")
  public @ResponseBody Iterable<HitopOrder> getAllOrders() {
    // This returns a JSON or XML with the users
    return hitopOrderRepository.findAll();
  }
}
