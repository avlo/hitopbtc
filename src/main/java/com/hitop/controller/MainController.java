package com.hitop.controller;

/*
 *  Copyright 2020 Nick Avlonitis
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */    

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;
import com.hitop.entity.PurchaseOrder;
import com.hitop.repository.PurchaseOrderRepository;
import com.hitop.service.PurchaseOrderService;
import com.hitop.service.QRCodeService;
import com.hitop.service.RateService;
import com.hitop.service.TransactionWrapper;
import com.hitop.service.WalletService;

@Controller
@RequestMapping(path="/")
public class MainController implements ReceiptListener {
  private final static Logger log = LoggerFactory.getLogger(MainController.class);
  
  private final static long SSE_EMITTER_TIMEOUT = 1200000l;  // hard-code 2min for now.  can add to application.properties if/when ready
  private final String productName;

  @Autowired
  private PurchaseOrderRepository orderRepository;

  @Autowired
  private WalletService walletService;

  @Autowired
  private QRCodeService qrCodeService;
  
  @Autowired
  private RateService rateService;
  
  @Autowired
  private PurchaseOrderService purchaseOrderService;
  
  Map<String, SseEmitter> sseEmitterMap = new HashMap<>();
  
  public MainController(final @Value("${productname}") String productName) {
    this.productName = productName;
  }

  @GetMapping("/")
  public String getIndexHtml(final Model model) throws Exception {
    model.addAttribute("productname", this.productName);
    return "index";
  }
  
  @GetMapping("/orderdetails")
  public String getOrderDetails(final Model model) {
    //TODO: add below for displaying current bitcoin rate to UI
//    order.setRateService.getUsdtoBtc(rateService.getBtcRate())));
    model.addAttribute("order", new PurchaseOrder());
    model.addAttribute("productname", this.productName);
    return "orderdetails";
  }

  @PostMapping("/ordersubmit")
  public String displayQR(final PurchaseOrder order, final BindingResult result, final Model model) throws Exception {
    order.setSendToAddress(walletService.getFreshSendToAddress());
    final PurchaseOrder savedPurchaseOrder = purchaseOrderService.save(order);
    model.addAttribute("order", savedPurchaseOrder);
    model.addAttribute("productname", this.productName);
    model.addAttribute("qrcodeurl", qrCodeService.getQRCodeUrl(savedPurchaseOrder.getSendToAddress()));
    // TODO 70 : call appropriate ordersubmit.html file based on stub/test/etc
    return "ordersubmit";
  }
  
  @GetMapping("/receipt-sse/{sendToAddress}")
  public SseEmitter setupSseEmitter(@PathVariable String sendToAddress) {
    SseEmitter emitter = new SseEmitter(SSE_EMITTER_TIMEOUT);
    sseEmitterMap.put(sendToAddress, emitter);
    return emitter;
  }

  public PurchaseOrder displayReceiptSse(final TransactionWrapper transaction) {
    final String sendToAddress = walletService.getTxReceiveAddress(transaction);
    PurchaseOrder order = purchaseOrderService.findBySendToAddress(sendToAddress);
    // TODO: is below newSingleThreadExecutor() the correct method to use?
    ExecutorService executor = Executors.newSingleThreadExecutor(); // Executors.newCachedThreadPool();
    executor.execute(() -> {
      try {
        SseEventBuilder event = SseEmitter.event()
            .data("{\"name\":\"" + order.getName());
        //  .data("{\"email\":\"" + order.getEmail());
        //   etc
        this.sseEmitterMap.get(sendToAddress).send(event);
        this.sseEmitterMap.get(sendToAddress).complete();
      } catch (Exception e) {
        log.info("##### EMITTER EXCEPTION: " + e.toString());
        this.sseEmitterMap.get(sendToAddress).completeWithError(e);
      } finally {
        this.sseEmitterMap.remove(sendToAddress);
      }
    });
    executor.shutdown();
    return order;
  }

  //TODO: wrap below in security so only admin can call it
  @GetMapping(path="/allordershitop")
  public @ResponseBody Iterable<PurchaseOrder> getAllOrders() {
    return orderRepository.findAll();
  }
}
