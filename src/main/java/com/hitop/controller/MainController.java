package com.hitop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.hitop.entity.HitopOrder;
import com.hitop.repository.HitopOrderRepository;
import com.hitop.service.WalletService;

@Controller
@RequestMapping(path="/")
public class MainController {
	
  @Autowired
  private HitopOrderRepository hitopOrderRepository;
  
  @Autowired
  private WalletService walletService;
  
  public MainController() throws Exception {
//    getTransactionJson("");
  }
  
  @GetMapping(path="/orderscreen")
  public ModelAndView displayOrderForm () throws Exception {
    walletService.monitorReceiveEvent();
    ModelAndView modelAndView = new ModelAndView("order");
    modelAndView.addObject("walletid", walletService.getQRCodeUrl());
    return modelAndView;
  }
  
  @GetMapping(path="/all")
  public @ResponseBody Iterable<HitopOrder> getAllOrders() {
    // This returns a JSON or XML with the users
    return hitopOrderRepository.findAll();
  }
}
