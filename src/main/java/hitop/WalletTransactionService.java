package hitop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletTransactionService {
  
  @Autowired
  private HitopOrderRepository hitopOrderRepository;
  
  @Autowired
  private BtcRateService btcRateService;

  public String addNewOrder () {
    System.out.println("3333333333333333");
    System.out.println("3333333333333333");
    System.out.println("3333333333333333");
    return "BTC event received";
  }
  
  public String addNewOrder (
      String name,
      String email,
      String address,
      String city,
      String state,
      String zip,
      String country,
      String btcPublicKey,
      String btcTransaction,
      Integer status) 
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
    return "Saved\n\n";
  }
}
