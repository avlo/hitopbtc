package hitop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(path="/")
public class MainController {
	
  @Autowired
  private HitopOrderRepository hitopOrderRepository;
  
  @Autowired
  private WalletMonitorService monitorWalletService;
  
  public MainController() throws Exception {
//    getTransactionJson("");
  }
  
  @GetMapping(path="/orderscreen")
  public ModelAndView displayOrderForm () throws Exception {
    monitorWalletService.monitorEvent();
    ModelAndView modelAndView = new ModelAndView("order");
    modelAndView.addObject("walletid", monitorWalletService.qrCodeApi());
    return modelAndView;
  }
  
  @GetMapping(path="/all")
  public @ResponseBody Iterable<HitopOrder> getAllOrders() {
    // This returns a JSON or XML with the users
    return hitopOrderRepository.findAll();
  }
}
