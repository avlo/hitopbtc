package com.hitop.service.stub;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.hitop.service.QRCodeService;

@Service
@ConditionalOnProperty(
    name = "spring.profiles.active", 
    havingValue = "dev")
public class StubQRCodeService implements QRCodeService {

  @Override
  public String getQRCodeUrl(String sendToAddress) {
    return "STUB QR CODE URL";
  }
}
